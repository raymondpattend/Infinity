package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.mysql.MySQL;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.entities.Role;
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
        Server server = Server.getServer(event.getGuild());
        if (event.getMember().getUser().isBot()) return;
        try {
            MySQL.createMember(event.getMember(), event.getGuild());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            if (server.getSettings().isJoin_message_enabled()) {
                event.getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage(server.getSettings().getJoin_message().replace("%guild_name%", event.getGuild().getName())).queue();
                });
            }

            if (server.getSettings().isJoin_role_enabled()) {
                try {
                    if (server.getSettings().getJoin_role_id() == 0) return;
                    Role role = event.getGuild().getRoleById(server.getSettings().getJoin_role_id());
                    if (role==null) return;
                    event.getGuild().addRoleToMember(event.getMember(), role).queue();
                } catch (NullPointerException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        } catch (NullPointerException | HierarchyException e){
            e.printStackTrace();
        }
    }
}
