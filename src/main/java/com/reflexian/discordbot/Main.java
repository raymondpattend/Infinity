package com.reflexian.discordbot;

import com.reflexian.discordbot.chat.AntiSwear;
import com.reflexian.discordbot.events.guildevents.GuildJoinCaptcha;
import com.reflexian.discordbot.events.runnables.PlayerCounter;
import com.reflexian.discordbot.listeners.CommandListener;
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

    private static Long now = System.currentTimeMillis();
    private static JDA jda;
    private static Main plugin;
    public static Date lastRestart;
    public static Map<Long, DiscordUser> discordUserMap = new HashMap<>();
    public static final Logger logger = LoggerFactory.getLogger(Main.class);


    private Connection connection;
    public String host, database, username, password, table;
    public int port;

    public static void main(String[] args) throws IOException, URISyntaxException {


        new Main().mysqlSetup();
        AntiSwear.saveTheList();

        lastRestart = new Date();
        now = System.currentTimeMillis();

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


            jda.awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }

        if (isDev) logger.warn("Starting up in Developer Mode.");

        PlayerCounter playerCounter = new PlayerCounter();
        (new Thread(playerCounter)).start();

    }

    public void mysqlSetup(){
        this.plugin = this;
        host = "181.215.242.76";
        port = 15232;
        database = "Prison";
        username = "Raymond";
        password = "Midland12";
        table = "null";

        try{

            synchronized (this){
                if(getConnection() != null && !getConnection().isClosed()){
                    return;
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                setConnection( DriverManager.getConnection("jdbc:mysql://" + this.host + ":"
                        + this.port + "/" + this.database, this.username, this.password+""));
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
