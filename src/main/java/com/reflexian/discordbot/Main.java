package com.reflexian.discordbot;

import com.reflexian.discordbot.chat.AntiSwear;
import com.reflexian.discordbot.events.guildevents.*;
import com.reflexian.discordbot.events.messages.GuildMessage;
import com.reflexian.discordbot.events.log.MessageLoader;
import com.reflexian.discordbot.events.runnables.Data;
import com.reflexian.discordbot.events.runnables.PlayerCounter;
import com.reflexian.discordbot.listeners.CommandListener;
import com.reflexian.discordbot.mysql.MySQL;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.CommunicationException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Main {

    // TODO Change from true to false
    public double version = 0.0204;
    public static boolean isDev = false;

    private static JDA jda;
    private static Main plugin;
    public static boolean fullyEnabled;
    public static Date lastRestart;
    public static final Logger logger = LoggerFactory.getLogger(Main.class);


    private Connection connection;
    public String host, database, username, password, table;
    public int port;

    public static void main(String[] args) throws IOException, URISyntaxException, SQLException {

        fullyEnabled=false;

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("Shutting down...");
                for (Server server : Server.SERVER_MAP.values()) {
                    if (!server.isUpdateToDatabase()) continue;
                    server.getSettings().setMySQLValues();
                }
            }
        }, "Shutdown-thread"));

        new Main().mysqlSetup();
        //AntiSwear.saveTheList();

        lastRestart = new Date();

        JDABuilder jdaBuilder;
        if (isDev) jdaBuilder = JDABuilder.createDefault("Nzg0NTE1MTc2MTMyMzc4NjI1.X8qasQ.Mk1eT9s-eeWEWjYt0D-gQ3wLZkU");
        // DEV TOKEN ^^^ NORMAL TOKEN VVV
        else jdaBuilder = JDABuilder.createDefault("Nzc1MjUwMDYxNTA0NDEzNzI3.X6jl4g.YM41k89HRThaAckRDx4XD5DM-MU");
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES).setMemberCachePolicy(MemberCachePolicy.ALL).setChunkingFilter(ChunkingFilter.ALL);
        try {
            jda = jdaBuilder.build();

            jda.addEventListener(new CommandListener());
            jda.addEventListener(new AntiSwear("antisw"));
            jda.addEventListener(new GuildJoinCaptcha());
            jda.addEventListener(new GuildJoinEvent());
            jda.addEventListener(new GuildLeaveEvent());
            jda.addEventListener(new BotAdded());
            jda.addEventListener(new MessageLoader());
            jda.addEventListener(new BotRemoved());
            jda.addEventListener(new GuildMessage());

            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        if (isDev) logger.warn("Starting up in Developer Mode.");

        new MySQL().registerTables();
        PlayerCounter playerCounter = new PlayerCounter();
        Data data = new Data();
        (new Thread(playerCounter)).start();
        (new Thread(data)).start();

    }

    public void mysqlSetup(){
        this.plugin = this;
        host = "na01-sql.pebblehost.com";
        port = 3306;
        database = "customer_145723_infinity";
        username = "customer_145723_infinity";
        password = "Magicfly1234$";
        table = "null";

        try{

            synchronized (this){
                if(getConnection() != null && !getConnection().isClosed()){
                    return;
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                //setConnection( DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password+"&serverTimezone=UTC"));
                setConnection(DriverManager.getConnection("jdbc:mysql://customer_145723_infinity:Magicfly1234$@na01-sql.pebblehost.com/customer_145723_infinity?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"));
                Main.logger.info("Attempting MySQL Login...");
            }
            Main.logger.info("MySQL Login Successful!");
        }catch(SQLException | ClassNotFoundException e){
            Main.logger.error("Failed to connect to the MySQL Database. " + e.getLocalizedMessage());
        }
        Main.logger.info("Attempting Bot Login...");
    }
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private int i = 0;
    public ResultSet executeQuery(String sql, boolean retry) throws SQLException {
        ResultSet resultSet = null;
        i++;
        try {
            resultSet = getConnection().createStatement().executeQuery(sql);
        } catch (Exception e) {
            // disconnection or timeout error
            if (retry && e instanceof CommunicationException || (e instanceof SQLException && e.toString().contains("Could not retrieve transation read-only status server"))) {
                // connect again
                mysqlSetup();
                // recursive, retry=false to avoid infinite loop
                return executeQuery(sql,false);
            }else{
                throw e;
            }
        }
        return resultSet;
    }

    public static JDA getJda() {
        return jda;
    }

    public static Main getPlugin() {
        return plugin;
    }
}
