package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Unban extends Command {

    public Unban(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.BAN_MEMBERS)) {
            event.getChannel().sendMessage(new EmbedBuilder().setTitle("No permission.").setDescription("You need ``BAN_MEMBERS`` permission to use this command!").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }
        if (args.length <3) {
            event.getChannel().sendMessage(new EmbedBuilder().setTitle("Not enough arguments.").setDescription("Correct usage:\n``@Infinity#9833 ban <mention / id> <reason>``").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }
        try {
            event.getGuild().unban(args[2]).queue(unused -> {
                event.getChannel().sendMessage(new EmbedBuilder().setTitle("Successfully unbanned.").setDescription("You have successfully unbanned " + args[2] + "!").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(12, 137, 33)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            }, (failure) -> {
                event.getChannel().sendMessage(new EmbedBuilder().setTitle("Not a valid user ID.").setDescription("You must include a valid user id or the specified user ID is not banned.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            });
        }catch (NullPointerException | IllegalArgumentException e) {
            event.getChannel().sendMessage(new EmbedBuilder().setTitle("Not a valid user ID.").setDescription("You must include a valid user id or the specified user ID is not banned.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
        }

    }

    @Override
    public void cancel() {

    }
}
