package com.reflexian.discordbot.utilities.objects;

import com.reflexian.discordbot.Main;

import java.sql.SQLException;
import java.sql.Statement;

public class Settings {

    public String table = "guild_data";

    private boolean join_message_enabled;
    private boolean join_role_enabled;
    private boolean level_enabled;
    private boolean logging_enabled;
    private boolean antiswear_enabled;
    private boolean counting_enabled;
    private boolean verified;

    private String join_message;
    private String level_message;
    private String level_roles;
    private String blacklisted_words;

    private long logging_channel;
    private long counting_channel;
    private long join_role_id;
    private long level_channel;
    private long counting_number;

    private Server server;

    public Settings(Server server) {
        this.server=server;
    }

    public void setMySQLValues() {
        try {
            Statement statement = Main.getPlugin().getConnection().createStatement();
            // BOOLEAN
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "join_message_enabled", this.join_message_enabled ? 1 : 0, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "join_role_enabled", this.join_role_enabled ? 1 : 0, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "level_enabled", this.level_enabled ? 1 : 0, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "logging_enabled", this.logging_enabled ? 1 : 0, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "verified", this.verified ? 1 : 0, "guild_id", this.getServer().getGuild().getId()));
            // STRINGS
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "join_message", this.join_message, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "level_message", this.level_message, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "level_roles", this.level_roles, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "blacklisted_words", this.blacklisted_words, "guild_id", this.getServer().getGuild().getId()));
            // LONGS
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "logging_channel", this.logging_channel, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "join_role_id", this.join_role_id, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "level_channel", this.level_channel, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "counting_channel", this.counting_channel, "guild_id", this.getServer().getGuild().getId()));
            statement.addBatch(String.format("UPDATE %s SET `%s` = '%s' WHERE %s='%s';", table, "counting_number", this.counting_number, "guild_id", this.getServer().getGuild().getId()));

            statement.executeBatch();
            this.getServer().setUpdateToDatabase(false);
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isJoin_message_enabled() {
        return join_message_enabled;
    }
    public boolean isJoin_role_enabled() {
        return join_role_enabled;
    }
    public boolean isLevel_enabled() {
        return level_enabled;
    }
    public boolean isLogging_enabled() {
        return logging_enabled;
    }
    public boolean isVerified() {
        return verified;
    }
    public boolean isCounting_enabled() {
        return counting_enabled;
    }

    public long getJoin_role_id() {
        return join_role_id;
    }
    public long getLevel_channel() {
        return level_channel;
    }
    public long getLogging_channel() {
        return logging_channel;
    }
    public long getCounting_channel() {
        return counting_channel;
    }
    public long getCounting_number() {
        return counting_number;
    }

    public String getBlacklisted_words() {
        return blacklisted_words;
    }
    public String getJoin_message() {
        return join_message;
    }
    public String getLevel_message() {
        return level_message;
    }
    public String getLevel_roles() {
        return level_roles;
    }

    public Server getServer() {
        return server;
    }

    public void setBlacklisted_words(String blacklisted_words) {
        this.server.setUpdateToDatabase(true);
        this.blacklisted_words = blacklisted_words;
    }
    public void setJoin_message(String join_message) {
        this.server.setUpdateToDatabase(true);
        this.join_message = join_message;
    }
    public void setJoin_message_enabled(boolean join_message_enabled) {
        this.server.setUpdateToDatabase(true);
        this.join_message_enabled = join_message_enabled;
    }
    public void setJoin_role_enabled(boolean join_role_enabled) {
        this.server.setUpdateToDatabase(true);
        this.join_role_enabled = join_role_enabled;
    }
    public void setJoin_role_id(long join_role_id) {
        this.server.setUpdateToDatabase(true);
        this.join_role_id = join_role_id;
    }
    public void setLevel_channel(long level_channel) {
        this.server.setUpdateToDatabase(true);
        this.level_channel = level_channel;
    }
    public void setLevel_enabled(boolean level_enabled) {
        this.server.setUpdateToDatabase(true);
        this.level_enabled = level_enabled;
    }
    public void setLevel_message(String level_message) {
        this.server.setUpdateToDatabase(true);
        this.level_message = level_message;
    }
    public void setLevel_roles(String level_roles) {
        this.server.setUpdateToDatabase(true);
        this.level_roles = level_roles;
    }
    public void setLogging_channel(long logging_channel) {
        this.server.setUpdateToDatabase(true);
        this.logging_channel = logging_channel;
    }
    public void setLogging_enabled(boolean logging_enabled) {
        this.server.setUpdateToDatabase(true);
        this.logging_enabled = logging_enabled;
    }
    public void setVerified(boolean verified) {
        this.server.setUpdateToDatabase(true);
        this.verified = verified;
    }
    public void setCounting_channel(long counting_channel) {
        this.server.setUpdateToDatabase(true);
        this.counting_channel = counting_channel;
    }
    public void setCounting_enabled(boolean counting_enabled) {
        this.server.setUpdateToDatabase(true);
        this.counting_enabled = counting_enabled;
    }
    public void setCounting_number(long counting_number) {
        this.server.setUpdateToDatabase(true);
        this.counting_number = counting_number;
    }
}
