package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.commands.fun.Embed;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.SQLException;

public class BotAdded extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Main.logger.info("Joined " + event.getGuild().getName() + ".");
        int i = 0;
        for (Member member : event.getGuild().getMembers()) {
            if (member.getUser().isBot()) i++;
            if (i>25) {
                EmbedBuilder em = new EmbedBuilder();
                em.setColor(new Color(220, 7, 25));
                em.setTitle("And... I'm out.");
                em.setDescription("Your discord server contains too many bots. Because of this, I have no choice but to leave this server. You are welcome to re-add me when you lower the amount of bots already here.");
                em.setFooter("Created by https://www.reflexian.com");
                TextChannel textChannel = event.getGuild().getTextChannels().get(0);
                textChannel.sendMessage(em.build()).queue(message -> {
                    Main.logger.warn("Leaving " + event.getGuild().getName() + " because they have more than 20 bots.");
                    event.getGuild().leave().queue();
                }, (failure) -> {
                    Main.logger.error("Failed to send message in " + event.getGuild().getName()+ ". (" + failure.getLocalizedMessage()+")");
                });
                return;
            }
        }
        try {
            MySQL.createGuild(event.getGuild());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        EmbedBuilder em = new EmbedBuilder();
        em.setColor(new Color(7, 211, 220));
        em.setTitle("Hello World!");
        em.setDescription("Thank you for adding me! I'm so excited to work with you to help make your discord server better!!! Lets get started with basic usages:\n**Prefix**\nMy prefix is simply mentioning me (or @Infinity#9833)\n**Commands**\nYou can see all the commands using \"@Infinity#9833 help\".\n**Need Help?**\nIf you need help, join [our discord](https://discord.gg/WpCpRbC).");
        em.addField("WARNING","Please make sure my role is higher than all other roles, so I can assign roles to new members!\nServer Settings > Roles > Drag \"Infinity\" to the top.", false);
        em.setThumbnail(event.getGuild().getIconUrl());
        em.setFooter("Created by https://www.reflexian.com.");

        TextChannel textChannel = event.getGuild().getTextChannels().get(0);
        textChannel.sendMessage(em.build()).queue();
    }
}
