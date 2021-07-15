package com.reflexian.discordbot.events.threads;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLThread extends Thread{

    public void run() {
        boolean finished = false;
        while (!finished) {
            try {
                Main.logger.info("Reloading all guilds Infinity is in...");
                for (Guild guild : Main.getJda().getGuilds()) {
                    PreparedStatement ps = Main.getPlugin().getConnection().prepareStatement("SELECT * FROM guild_data WHERE guild_id = " + guild.getIdLong());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        if (Server.SERVER_MAP.containsKey(guild.getIdLong())) continue;
                        Server server = new Server(guild);
                        Server.SERVER_MAP.put(guild.getIdLong(), server);
                    } else {
                        PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                                .prepareStatement("INSERT IGNORE INTO guild_data(guild_id,join_message_enabled,join_message,join_role_enabled,join_role_id) VALUES (?,?,?,?,?)");
                        preparedStatement.setLong(1, guild.getIdLong());
                        preparedStatement.setInt(2, 0);
                        preparedStatement.setString(3, "Welcome to %guild_name%! Make sure to read the rules...");
                        preparedStatement.setInt(4, 0);
                        preparedStatement.setLong(5, 0);
                        preparedStatement.execute();
                        System.out.println("Added Guild \"" + guild.getName() + "\"");
                        if (Server.SERVER_MAP.containsKey(guild.getIdLong())) continue;
                        Server server = new Server(guild);
                        Server.SERVER_MAP.put(guild.getIdLong(), server);
                    }
                }
                Main.fullyEnabled=true;
                System.out.println("Finished reloading all guilds.");
                finished=true;
            }catch (SQLException nor) {
                nor.printStackTrace();
            }
        }
    }
}
