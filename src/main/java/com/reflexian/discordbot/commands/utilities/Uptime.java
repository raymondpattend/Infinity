package com.reflexian.discordbot.commands.utilities;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Uptime extends Command {

    private String getTime(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    private String getTimeDiff(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return diffDays + "d " + parseTimeNumbs(diffHours) + "h " + parseTimeNumbs(diffMinutes) + "m " + parseTimeNumbs(diffSeconds) + "s";
    }

    private String parseTimeNumbs(long time) {
        return time + "";
    }


    public Uptime(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(new Color(73, 208, 109))
                        .setTitle("Discord Bot Uptime")
                        .addField("Last restart", getTime(Main.lastRestart, "dd/MM/yyyy : HH:mm:ss (z)"), false)
                        .addField("Online for", getTimeDiff(new Date(), Main.lastRestart), false)
                        .setFooter(("Requested by " + event.getAuthor().getAsTag()) , event.getAuthor().getAvatarUrl())
                        .build()
        ).queue(message1 -> message1.delete().queueAfter(20, TimeUnit.SECONDS));
    }

    @Override
    public void cancel() {

    }
}
