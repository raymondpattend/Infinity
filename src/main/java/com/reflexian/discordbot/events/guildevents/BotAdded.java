package com.reflexian.discordbot.events.guildevents;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class BotAdded extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {

        EmbedBuilder em = new EmbedBuilder();
        em.setColor(new Color(7, 211, 220));
        em.setTitle("Hello World!");
        em.setDescription("Thank you for adding me! I'm so excited to work with you to help make your discord server better!!! Lets get started with basic usages:\n**Prefix**\nMy prefix is simply mentioning me (or @Infinity#9833)\n**Commands**\nYou can see all the commands using \"@Infinity#9833 help\".\n**Need Help?**\nIf you need help, join [our discord](https://discord.gg/WpCpRbC).");
        em.setThumbnail(event.getGuild().getIconUrl());
        em.setFooter("Created by https://www.reflexian.com.");

        TextChannel textChannel = event.getGuild().getTextChannels().get(0);
        textChannel.sendMessage(em.build()).queue();
    }
}
