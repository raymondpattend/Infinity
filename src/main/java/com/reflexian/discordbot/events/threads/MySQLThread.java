package com.reflexian.discordbot.events.threads;

import com.reflexian.discordbot.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MySQLThread extends Thread{

    public void run() {
        long startTime = System.currentTimeMillis();
        int users = 0;
        boolean finished = false;
        while (!finished) {
            try {
                PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                        .prepareStatement("INSERT IGNORE INTO guild_data(guild_id,join_message_enabled,join_message,join_role_enabled,join_role_id) VALUES (?,?,?,?,?)");
                for (Guild guild : Main.getJda().getGuilds()) {
                    preparedStatement.setLong(1, guild.getIdLong());
                    preparedStatement.setInt(2, 0);
                    preparedStatement.setString(3, "Welcome to %guild_name%! Make sure to read the rules...");
                    preparedStatement.setInt(4, 0);
                    preparedStatement.setLong(5, 0);
                    preparedStatement.execute();
                    //Main.logger.info("" + getString("guild_data", "guild_name", "guild_id", String.valueOf(guild.getIdLong())) + " !!!!");
                }
                Main.logger.info("Successfully loaded all guild data (" + ((long)((System.currentTimeMillis()-startTime) / 1000) % 60)+"s - " + users + " users)");
                finished=true;

            }catch (SQLException nor) {
                nor.printStackTrace();
            }
        }
    }
}
