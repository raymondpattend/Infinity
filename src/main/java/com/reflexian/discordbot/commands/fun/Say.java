package com.reflexian.discordbot.commands.fun;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Say extends Command {

    public Say(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    private Map<Member, Long> memberMap = new HashMap<>();

    @Override
    public void execute(MessageReceivedEvent event) {

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            sendMessage(event.getTextChannel(), "Sorry, but you need ``MANAGE_MESSAGES`` permission to use this command, " + event.getAuthor().getAsMention()+"!", 15);
            return;
        }

        try {
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            StringBuilder str = new StringBuilder();
            for (String string : args) {
                if (string.equals(args[0])||string.equals(args[1])) {
                    continue;
                }
                str.append(string).append(" ");
            }

            if (str.length() >250) {
                sendMessage(event.getTextChannel(), "You cannot send messages longer than 250 characters, " + event.getAuthor().getAsMention()+".", 15);
                return;
            }

            if (str.toString().contains("@everyone")||str.toString().contains("@here")) {
                sendMessage(event.getTextChannel(), "You cannot mention ``@everyone`` or ``@here``!", 15);
                return;
            }

            if (str.toString().contains("*")) {
                int startIndex = str.indexOf("*");
                int endIndex = str.indexOf(" ", startIndex);
                if (endIndex == -1) {
                    endIndex = str.length();
                }
                String timer = str.substring(startIndex+1, endIndex);
                str = str.delete(startIndex, endIndex);
                int lon;
                try {
                    lon = Integer.parseInt(timer);
                }catch (NumberFormatException e) {
                    sendMessage(event.getTextChannel(), event.getAuthor().getAsTag() + "**:** " + str.replace(startIndex, endIndex, "").toString(), null);
                    return;
                }

                sendMessage(event.getTextChannel(),event.getAuthor().getAsTag() + "**:** " +  str.toString(), lon);
                return;

            }
            sendMessage(event.getTextChannel(), event.getAuthor().getAsTag() + "**:** " +  str.toString(), null);
        } catch (IllegalStateException | IllegalArgumentException e) {
            sendMessage(event.getTextChannel(), "Command must include text!\n**Example**: ``@Infinity#9388 say hello *30``", 20);
        }
    }

    @Override
    public void cancel() {

    }
}
