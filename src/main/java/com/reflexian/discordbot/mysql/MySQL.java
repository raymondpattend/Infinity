package com.reflexian.discordbot.mysql;

import com.reflexian.discordbot.Main;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {

    public void registerTables() throws SQLException {
        Statement statement = Main.getPlugin().getConnection().createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `guild_data` (`guild_id` bigint(18) unsigned NOT NULL, `join_message_enabled` tinyint(4) NOT NULL DEFAULT '0', `join_message` varchar(250) NOT NULL DEFAULT 'Welcome to %guild_name%! Make sure to read the rules.', `join_role_enabled` tinyint(4) NOT NULL DEFAULT '0', `join_role_id` int(18) DEFAULT NULL, PRIMARY KEY (`guild_id`))");

        for (Guild guild : Main.getJda().getGuilds()) {
            long guildid = guild.getIdLong();
            Main.logger.info("Updating " + guild.getName() + " " + guild.getIdLong());
            statement.executeUpdate("INSERT IGNORE INTO `guild_data` SET `guild_id` = '" + guildid +  "';");
        }

    }

}
