package com.reflexian.discordbot.mysql;

import com.reflexian.discordbot.Main;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySQL {

    private List<Long> cachedGuilds = new ArrayList<>();

    public void registerTables() throws SQLException {
        cachedGuilds.clear();
        Statement statement = Main.getPlugin().getConnection().createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `guild_data` (`guild_id` bigint(20) NOT NULL, `guild_name` varchar(250) DEFAULT NULL, `join_message_enabled` int(4) NOT NULL DEFAULT '0', `join_message` varchar(250) NOT NULL DEFAULT 'Welcome to %guild_name%! Make sure to read the rules.', `join_role_enabled` tinyint(4) NOT NULL DEFAULT '0', `join_role_id` bigint(20) DEFAULT NULL, PRIMARY KEY (`guild_id`))");

        PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                .prepareStatement("INSERT IGNORE INTO guild_data(guild_id,guild_name,join_message_enabled,join_message,join_role_enabled,join_role_id) VALUES (?,?,?,?,?,?)");
        for (Guild guild : Main.getJda().getGuilds()) {
            cachedGuilds.add(guild.getIdLong());
            preparedStatement.setLong(1, guild.getIdLong());
            preparedStatement.setString(2, guild.getName() + " AAAAAAA");
            preparedStatement.setInt(3, 0);
            preparedStatement.setString(4, "Welcome to %guild_name%! Make sure to read the rules...");
            preparedStatement.setInt(5, 0);
            preparedStatement.setLong(6, 0);
            preparedStatement.execute();

        }
    }

    // UPDATE guild_data SET join_role_id = 12345 WHERE guild_id = 770142850633433093;
    // Updates value join_role_id where guild_id = 770142850633433093;
}
