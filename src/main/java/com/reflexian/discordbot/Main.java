package com.reflexian.discordbot;

import com.reflexian.discordbot.chat.AntiSwear;
import com.reflexian.discordbot.events.guildevents.BotAdded;
import com.reflexian.discordbot.events.guildevents.GuildJoinCaptcha;
import com.reflexian.discordbot.events.guildevents.GuildJoinEvent;
import com.reflexian.discordbot.events.runnables.PlayerCounter;
import com.reflexian.discordbot.listeners.CommandListener;
import com.reflexian.discordbot.mysql.MySQL;
import com.reflexian.discordbot.utilities.DiscordUser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {

    // TODO Change from true to false
    public static boolean isDev = false;

    private static JDA jda;
    private static Main plugin;
    public static Date lastRestart;
    public static Map<Long, DiscordUser> discordUserMap = new HashMap<>();
    public static final Logger logger = LoggerFactory.getLogger(Main.class);


    private Connection connection;
    public String host, database, username, password, table;
    public int port;

    public static void main(String[] args) throws IOException, URISyntaxException, SQLException {


        new Main().mysqlSetup();
        AntiSwear.saveTheList();

        lastRestart = new Date();

        JDABuilder jdaBuilder;
        if (isDev) jdaBuilder = JDABuilder.createDefault("Nzg0NTE1MTc2MTMyMzc4NjI1.X8qasQ.Mk1eT9s-eeWEWjYt0D-gQ3wLZkU");
        // DEV TOKEN ^^^ NORMAL TOKEN VVV
        else jdaBuilder = JDABuilder.createDefault("Nzc1MjUwMDYxNTA0NDEzNzI3.X6jl4g.qluBVSJ6yEBusW5iFvrMOVVIY_Q");
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES).setMemberCachePolicy(MemberCachePolicy.ALL).setChunkingFilter(ChunkingFilter.ALL);
        try {
            jda = jdaBuilder.build();

            jda.addEventListener(new CommandListener());
            jda.addEventListener(new AntiSwear("antisw"));
            jda.addEventListener(new GuildJoinCaptcha());
            jda.addEventListener(new GuildJoinEvent());
            jda.addEventListener(new BotAdded());


            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        if (isDev) logger.warn("Starting up in Developer Mode.");

        new MySQL().registerTables();
        PlayerCounter playerCounter = new PlayerCounter();
        (new Thread(playerCounter)).start();

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


    public static JDA getJda() {
        return jda;
    }

    public static Main getPlugin() {
        return plugin;
    }
}
