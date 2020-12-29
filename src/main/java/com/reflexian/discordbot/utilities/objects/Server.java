package com.reflexian.discordbot.utilities.objects;

import com.reflexian.discordbot.Main;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Server {

    public static final Map<Long, Server> SERVER_MAP = new HashMap<>();

    private Guild guild;
    private Settings settings;

    private boolean updateToDatabase;

    public static Server getServer(Guild guild) {
        return SERVER_MAP.get(guild.getIdLong());
    }

    public Server(Guild guild) {
        this.guild = guild;
        this.settings = new Settings(this);
        try {
            PreparedStatement ps = Main.getPlugin().getConnection().prepareStatement("SELECT * FROM guild_data WHERE guild_id = " + this.guild.getId()+";");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                settings.setJoin_message_enabled(rs.getBoolean("join_message_enabled"));
                settings.setJoin_role_enabled(rs.getBoolean("join_role_enabled"));
                settings.setJoin_role_id(rs.getLong("join_role_id"));
                settings.setJoin_message(rs.getString("join_message"));
                settings.setLevel_enabled(rs.getBoolean("level_enabled"));
                settings.setLevel_channel(rs.getLong("level_channel"));
                settings.setLevel_message(rs.getString("level_message"));
                settings.setLevel_roles(rs.getString("level_roles"));
                settings.setLogging_enabled(rs.getBoolean("logging_enabled"));
                settings.setLogging_channel(rs.getLong("logging_channel"));
                settings.setVerified(rs.getBoolean("verified"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        this.updateToDatabase=true;
    }

    public Guild getGuild() {
        return guild;
    }
    public Settings getSettings() {
        return settings;
    }

    public boolean isUpdateToDatabase() {
        return updateToDatabase;
    }

    public void setUpdateToDatabase(boolean updateToDatabase) {
        this.updateToDatabase = updateToDatabase;
    }
}
