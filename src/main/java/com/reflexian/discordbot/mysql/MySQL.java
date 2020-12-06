package com.reflexian.discordbot.mysql;

import com.reflexian.discordbot.Main;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySQL {

    private static Connection connection = Main.getPlugin().getConnection();

    public void registerTables() throws SQLException {
        Statement statement = Main.getPlugin().getConnection().createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `guild_data` (`guild_id` bigint(20) NOT NULL, `guild_name` varchar(250) DEFAULT NULL, `join_message_enabled` int(4) NOT NULL DEFAULT '0', `join_message` varchar(250) NOT NULL DEFAULT 'Welcome to %guild_name%! Make sure to read the rules.', `join_role_enabled` tinyint(4) NOT NULL DEFAULT '0', `join_role_id` bigint(20) DEFAULT NULL, PRIMARY KEY (`guild_id`))");

        PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                .prepareStatement("INSERT IGNORE INTO guild_data(guild_id,guild_name,join_message_enabled,join_message,join_role_enabled,join_role_id) VALUES (?,?,?,?,?,?)");
        for (Guild guild : Main.getJda().getGuilds()) {
            preparedStatement.setLong(1, guild.getIdLong());
            preparedStatement.setString(2, guild.getName());
            preparedStatement.setInt(3, 0);
            preparedStatement.setString(4, "Welcome to %guild_name%! Make sure to read the rules...");
            preparedStatement.setInt(5, 0);
            preparedStatement.setLong(6, 0);
            preparedStatement.execute();
            //Main.logger.info("" + getString("guild_data", "guild_name", "guild_id", String.valueOf(guild.getIdLong())) + " !!!!");
        }
    }

    public static void createGuild(Guild guild) throws SQLException {
        PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                .prepareStatement("INSERT IGNORE INTO guild_data(guild_id,guild_name,join_message_enabled,join_message,join_role_enabled,join_role_id) VALUES (?,?,?,?,?,?)");
        preparedStatement.setLong(1, guild.getIdLong());
        preparedStatement.setString(2, guild.getName());
        preparedStatement.setInt(3, 0);
        preparedStatement.setString(4, "Welcome to %guild_name%! Make sure to read the rules...");
        preparedStatement.setInt(5, 0);
        preparedStatement.setLong(6, 0);
        preparedStatement.execute();
    }

    public static String getString(String table, String key ,String where, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement(String.format("SELECT * FROM %s WHERE %s = %s", table, where, value));
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean getBool(String table, String key ,String where, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement(String.format("SELECT * FROM %s WHERE %s = %s", table, where, value));
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getBoolean(key);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean setString(String table, String key, String value, String where, String wherevalue) {
        try {
            PreparedStatement ps;
            PreparedStatement check = connection.prepareStatement(String.format("SELECT * FROM %s WHERE %s = '%s'", table, where, wherevalue));

            if (check.executeQuery().next())
                ps = connection.prepareStatement(String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'", table, key, value, where, wherevalue));
            else
                ps = connection.prepareStatement(String.format("INSERT INTO %s (%s, %s) VALUES ('%s', '%s')", table, where, key, wherevalue, value));

            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static Boolean setBool(String table, String key, boolean value, String where, String wherevalue) {
        try {
            PreparedStatement ps;
            PreparedStatement check = connection.prepareStatement(String.format("SELECT * FROM %s WHERE %s = '%s'", table, where, wherevalue));

            if (check.executeQuery().next())
                ps = connection.prepareStatement(String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'", table, key, value ? 1 : 0, where, wherevalue));
            else
                ps = connection.prepareStatement(String.format("INSERT INTO %s (%s, %s) VALUES ('%s', '%s')", table, where, key, wherevalue, value ? 1 : 0));

            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static Boolean dropEntry(String table, String where, String wherevalue) {
        try {
            PreparedStatement ps;
            PreparedStatement check = connection.prepareStatement(String.format("SELECT * FROM %s WHERE %s = '%s'", table, where, wherevalue));

            if (check.executeQuery().next()) {
                ps = connection.prepareStatement(String.format("DELETE FROM %s WHERE %s = '%s'", table, where, wherevalue));
                ps.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    // UPDATE guild_data SET join_role_id = 12345 WHERE guild_id = 770142850633433093;
    // Updates value join_role_id where guild_id = 770142850633433093;
}
