package com.reflexian.discordbot.mysql;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.events.threads.MySQLThread;
import com.reflexian.discordbot.utilities.UtilStrings;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.*;

public class MySQL {

    private static Connection connection = Main.getPlugin().getConnection();

    public void registerTables() throws SQLException {
        Statement statement = Main.getPlugin().getConnection().createStatement();
        // guild_data table
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `guild_data` (`guild_id` bigint(18) NOT NULL, `guild_name` varchar(250) NOT NULL DEFAULT '', `join_message_enabled` int(4) NOT NULL DEFAULT '0', `join_message` varchar(250) NOT NULL DEFAULT 'Welcome to %guild_name%! Make sure to read the rules.', `join_role_enabled` tinyint(4) NOT NULL DEFAULT '0', `join_role_id` bigint(20) DEFAULT NULL, `level_enabled` int(4) NOT NULL DEFAULT '1', `level_channel` bigint(18) DEFAULT NULL, PRIMARY KEY (`guild_id`))");
        // user_data table
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `user_data` (`user_key` varchar(37) NOT NULL DEFAULT '', `leveling_level` bigint(20) NOT NULL DEFAULT '0', `leveling_xp` bigint(20) NOT NULL DEFAULT '0', `leveling_xpneeded` bigint(20) NOT NULL DEFAULT '100', PRIMARY KEY (`user_key`))");
        // membership table
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `memberships` (`guild_id` bigint(18) NOT NULL, `issued_time` bigint(60) NOT NULL, `issued_id` bigint(18) NOT NULL, `membership_id` int(11) NOT NULL, `description` varchar(250) NOT NULL DEFAULT '', PRIMARY KEY (`guild_id`))");
        statement.executeUpdate("DELETE FROM user_data WHERE leveling_xp = 0 AND leveling_level = 0");

        // New thread to save CPU and processing power
        // This saves all user data (causes lots of lag!)
        MySQLThread thread = new MySQLThread();
        thread.start();

    }

    public static void createGuild(Guild guild) throws SQLException {
        PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                .prepareStatement("INSERT IGNORE INTO guild_data(guild_id,join_message_enabled,join_message,join_role_enabled,join_role_id) VALUES (?,?,?,?,?)");
        preparedStatement.setLong(1, guild.getIdLong());
        preparedStatement.setInt(2, 0);
        preparedStatement.setString(3, "Welcome to %guild_name%! Make sure to read the rules...");
        preparedStatement.setInt(4, 0);
        preparedStatement.setLong(5, 0);
        preparedStatement.execute();
        Server server = new Server(guild);
        Server.SERVER_MAP.put(guild.getIdLong(), server);
    }

    public static void createMember(Member member, Guild guild) throws SQLException {
        PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                .prepareStatement("INSERT IGNORE INTO user_data(user_key,leveling_level,leveling_xp,leveling_xpneeded) VALUES (?,0,60,100)");
        preparedStatement.setString(1, member.getIdLong()+"#"+guild.getIdLong());
        preparedStatement.execute();
    }

    public static void deleteMember(Member member, Guild guild) throws SQLException {
        PreparedStatement preparedStatement = Main.getPlugin().getConnection().prepareStatement("SELECT * FROM user_data WHERE user_key = '"+member.getId()+"#"+guild.getId()+"';");
        PreparedStatement ps;
        if (preparedStatement.executeQuery().next()) {
            ps = connection.prepareStatement("DELETE FROM user_data WHERE user_key = '"+member.getId()+"#"+guild.getId()+"';");
            ps.execute();
        }
    }

    public static void registerMembership(Guild guild, Member executor, String description) throws SQLException {
        PreparedStatement ps = Main.getPlugin().getConnection()
                .prepareStatement("INSERT IGNORE INTO memberships(guild_id,issued_time,issued_id,membership_id,description) VALUES (?,?,?,?,?)");
        ps.setLong(1, guild.getIdLong());
        ps.setLong(2, System.currentTimeMillis());
        ps.setLong(3, executor.getIdLong());
        ps.setString(4, UtilStrings.generateRandomChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789", 11));
        ps.setString(5, description);
        ps.execute();
    }

    public static boolean hasMembership(Guild guild) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM memberships WHERE guild_id = " + guild.getId());
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public static String getString(String table, String key, String where, String value) {
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

    public static Integer getInteger(String table, String key ,String where, String value) {
        try {
            PreparedStatement ps = connection.prepareStatement(String.format("SELECT * FROM %s WHERE %s = %s", table, where, value));
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(key);
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

    public static Boolean setInteger(String table, String key, int value, String where, String wherevalue) {
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
