package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotRemoved extends ListenerAdapter {

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        MySQL.dropEntry("guild_data", "guild_id", event.getGuild().getId());
        System.out.println("Removed value " + event.getGuild().getId());
    }
}
