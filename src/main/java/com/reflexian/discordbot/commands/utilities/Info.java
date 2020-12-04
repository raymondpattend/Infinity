package com.reflexian.discordbot.commands.utilities;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Info extends Command {

    public Info(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        EmbedBuilder info = new EmbedBuilder();
        info.setColor(new Color(48, 144, 123));
        info.setTitle("Info");
        info.setDescription("This is the information about me!");

        int guilds = 0;
        int members = 0;

        for (Guild guild : Main.getJda().getGuilds()) {
            guilds++;
            members+=guild.getMemberCount();
        }

        info.addField("Total Guilds:Members", guilds+"/75:"+members, false);
        info.addField("My Creator", "Raymond (Rayrnond#0001:269262067503071232) originally created me for his discord server [Reflexian](https://discord.gg/WpCpRbC). I now stand proud helping people all over discord.", false);
        info.setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
        event.getChannel().sendMessage(info.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
    }

    @Override
    public void cancel() {

    }
}
