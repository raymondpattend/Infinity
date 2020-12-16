package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class GuildLeaveEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        try {
            MySQL.deleteMember(event.getMember(), event.getGuild());
        } catch (SQLException | NullPointerException throwables) {
            throwables.printStackTrace();
        }
    }
}
