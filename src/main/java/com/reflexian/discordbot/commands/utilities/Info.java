package com.reflexian.discordbot.commands.utilities;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Info extends Command {

    public Info(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(new Color(48, 144, 123));
        info.setTitle("Info (V" + Main.getPlugin().version+")");
        info.setDescription("This is the information about me! Learn more -> [Reflexian.com](https://www.reflexian.com/infinity)\nWant to help support Infinity? [Buy a sticker](https://www.reflexian.com/product/sticker/)!");

        int guilds = 0;

        int members = 0;
        for (Guild guild : Main.getJda().getGuilds()) {
            guilds++;
            for (Member member : guild.getMembers()) {
                if (member.getUser().isBot()) continue;
                members++;
            }
        }

        info.addField("Total Guilds / Members", "("+guilds+"/75) / "+members + " Users", false);
        info.addField("My Creator", "Raymond (Raymond#0001:269262067503071232)\nHello, my name is Raymond. I’m the CEO of Reflexian LLC and the Developer of Infinity. When developing Infinity, I thought about all the best parts of a Discord Bot; simple, fast, and the best possible experience for you. If one of those values were not in Infinity, this project wouldn’t have became public.\nFor support, please join our Discord -> [Reflexian](https://discord.gg/WpCpRbC).", false);
        StringBuilder s = new StringBuilder();
        int i = 1;
        for (Permission permission : event.getGuild().getSelfMember().getPermissions()) {
            if (event.getGuild().getSelfMember().getPermissions().size()==i) s.append("and ").append((permission.equals(Permission.ADMINISTRATOR) ? "**"+permission.getName()+"**" : permission.getName())).append(".");
            else s.append((permission.equals(Permission.ADMINISTRATOR) ? "**"+permission.getName()+"**" : permission.getName())).append(", ");
            i++;
        }
        info.addField("My Permissions", s.toString(), false);
        info.setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
        sendMessage(event.getTextChannel(), info.build(), 30);
    }

    @Override
    public void cancel() {

    }
}
