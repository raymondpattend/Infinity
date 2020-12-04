package com.reflexian.discordbot.commands.fun;

import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class Say extends Command {

    public Say(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        try {
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            StringBuilder str = new StringBuilder();
            for (String string : args) {
                if (string.equals(args[0])||string.equals(args[1])) {
                    continue;
                }
                str.append(string).append(" ");
            }

            if (str.toString().contains("*")) {
                int startIndex = str.indexOf("*");
                int endIndex = str.indexOf(" ", startIndex);
                if (endIndex == -1) {
                    endIndex = str.length();
                }
                String timer = str.substring(startIndex+1, endIndex);
                str = str.delete(startIndex, endIndex);
                long lon;
                try {
                    lon = Long.parseLong(timer);
                }catch (NumberFormatException e) {
                    event.getChannel().sendMessage(str.replace(startIndex, endIndex, "")).queue();
                    return;
                }

                event.getChannel().sendMessage(str).queue(message -> {
                    message.delete().queueAfter(lon, TimeUnit.SECONDS);
                });
                return;

            }
            event.getChannel().sendMessage(str).queue();
        } catch (IllegalStateException | IllegalArgumentException e) {
            event.getChannel().sendMessage("Must include text, " + event.getAuthor().getAsMention() + " :P").queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));

        }



    }

    @Override
    public void cancel() {

    }
}
