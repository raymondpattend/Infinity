package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BotRemoved extends ListenerAdapter {

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        MySQL.dropEntry("guild_data", "guild_id", event.getGuild().getId());
        try {
            PreparedStatement ps = Main.getPlugin().getConnection()
                    .prepareStatement("SELECT * FROM user_data WHERE user_key LIKE '%" + event.getGuild().getIdLong() + "%'");
            ResultSet rs =ps.executeQuery();
            while (rs.next()) {
                MySQL.dropEntry("user_data", "user_key", rs.getString("user_key"));
                System.out.println(rs.getString("user_key"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Infinity was removed from " + event.getGuild().getName());
    }
}
