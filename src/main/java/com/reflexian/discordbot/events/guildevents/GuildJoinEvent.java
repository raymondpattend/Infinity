package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildJoinEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        StringBuilder log = new StringBuilder();
        log.append(event.getMember().getUser().getAsTag()).append(" joined ").append(event.getGuild().getName()).append(".");
        try {
            PreparedStatement preparedStatement = Main.getPlugin().getConnection().prepareStatement("SELECT * FROM guild_data where guild_id='"+ event.getGuild().getIdLong() +"'");
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            if (rs.getInt("join_message_enabled")==1) {
                log.append(" Sent \"") .append(MySQL.getString("guild_data", "join_message", "guild_id", event.getGuild().getId())).append("\" to user.");
                event.getUser().openPrivateChannel().queue(privateChannel -> {
                    try {
                        privateChannel.sendMessage(rs.getString("join_message").replace("%guild_name%", event.getGuild().getName())).queue();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
            }

            if (rs.getInt("join_role_enabled")==1) {
                try {
                    log.append("Added role " + event.getGuild().getRoleById(rs.getLong("join_role_id")).getName()).append(".");
                    event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(rs.getLong("join_role_id"))).queue();
                } catch (NullPointerException | IllegalArgumentException a) {
                    log.append(" Failed to add role because of ").append(a.getLocalizedMessage()).append(".");
                }
            }

        } catch (SQLException | NullPointerException | HierarchyException ignored) {}
        Main.logger.info(log+"");
    }
}
