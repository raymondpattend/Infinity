package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.Main;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class GuildJoinEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        try {
            PreparedStatement preparedStatement = Main.getPlugin().getConnection().prepareStatement("SELECT * FROM guild_data where guild_id='"+ event.getGuild().getIdLong() +"'");
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            if (rs.getInt("join_message_enabled")==1) {
                event.getUser().openPrivateChannel().queue(privateChannel -> {
                    try {
                        privateChannel.sendMessage(rs.getString("join_message").replace("%guild_name%", event.getGuild().getName())).queue();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
            }

            if (rs.getInt("join_role_enabled")==1) {

                event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(rs.getLong("join_role_id")))).queue();

            }

        } catch (SQLException | NullPointerException | HierarchyException ignored) {}
    }
}
