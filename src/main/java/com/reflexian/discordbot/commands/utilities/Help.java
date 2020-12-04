package com.reflexian.discordbot.commands.utilities;

import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Help extends Command {


    public Help(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        EmbedBuilder help = new EmbedBuilder();
        help.setColor(new Color(67, 149, 19));
        help.setTitle("Help");
        if (event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            help.setDescription("These are the commands available to you! (PREFIX IS @Infinity#9833)\n\nhelp - You are here\ninfo - Information about the bot\nuptime - Uptime information\nban - Ban a player");
        } else {
            help.setDescription("These are the commands available to you! (PREFIX IS @Infinity#9833)\n\nhelp - You are here\ninfo - Information about the bot\nuptime - Uptime information");
        }
        help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
        event.getChannel().sendMessage(help.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
    }

    @Override
    public void cancel() {

    }
}
