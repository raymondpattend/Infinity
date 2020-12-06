package com.reflexian.discordbot.commands.utilities;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.utilities.UtilStrings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class User extends Command {
    public User(String[] command, @Nullable Member member, net.dv8tion.jda.api.entities.@Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args.length <3) {
            EmbedBuilder a = new EmbedBuilder();
            a.setColor(new Color(201, 43, 43));
            a.setTitle("Not enough arguments.");
            a.setDescription("You must include a mention or valid user id.\n**Example**: ``@Infinity9833 user 269262067503071232``");
            a.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            event.getChannel().sendMessage(a.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        Member member = null;

        try {
            if (event.getMessage().getMentionedUsers().size() == 2) {
                if (args[2].startsWith("<@") && args[2].endsWith(">")) {
                    member = event.getGuild().getMemberById(args[2].replace("!", "").replace("@", "").replace(">", "").replace("<", ""));
                }
            } else {
                member = event.getGuild().getMemberById(args[2]);
            }

            net.dv8tion.jda.api.entities.User user = member.getUser();

            EmbedBuilder player = new EmbedBuilder();
            player.setColor(new Color(60, 134, 191));
            player.setThumbnail(user.getAvatarUrl());
            player.setTitle("User Information: " + member.getUser().getAsTag());
            player.setDescription("Joined this guild on ``" + UtilStrings.formatOffsetDateTime(member.getTimeJoined()) + "``\nJoin Discord on ``" + UtilStrings.formatOffsetDateTime(user.getTimeCreated()) + "``");
            player.addField("Identity", "**Username** - " + user.getAsTag()+"\n**ID** - " + user.getId()+"\n**Nickname** - " + (member.getNickname() == null ? "N/A" : member.getEffectiveName()), false);
            try {
                player.addField("Status", member.getActivities().get(0) == null ? "N/A" : member.getActivities().get(0).getName(), false);
            }catch (IndexOutOfBoundsException e) {
                player.addField("Status", "N/A", false);
            }
            player.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            event.getChannel().sendMessage(player.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
        }catch (NullPointerException | NumberFormatException e) {
            event.getChannel().sendMessage(new EmbedBuilder().setTitle("Not a valid user.").setDescription("You must include a valid id or mention.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }



    }

    @Override
    public void cancel() {

    }
}
