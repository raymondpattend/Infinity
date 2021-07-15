package com.reflexian.discordbot.utilities;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UtilStrings {

    public static String formatDurationToString(Long duration) {
        TimeUnit u = TimeUnit.MILLISECONDS;
        long hours = u.toHours(duration) % 24;
        long minutes = u.toMinutes(duration) % 60;
        long seconds = u.toSeconds(duration) % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static String formatOffsetDateTime(OffsetDateTime time) {
        return DateTimeFormatter.ofPattern("M/d/u h:m:s a").format(time);
    }

    public static Color randomColor() {
        Random colorpicker = new Random();
        int red = colorpicker.nextInt(255) + 1;
        int green = colorpicker.nextInt(255) + 1;
        int blue = colorpicker.nextInt(255) + 1;
        return new Color(red, green, blue);
    }

    public static String generateRandomChars(String candidateChars, int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars
                    .length())));
        }
        return sb.toString();
    }

    public static String userDiscrimSet(User u) {
        return stripFormatting(u.getName()) + "#" + u.getDiscriminator();
    }

    public static String stripFormatting(String s) {
        return s.replace("*", "\\*")
                .replace("`", "\\`")
                .replace("_", "\\_")
                .replace("~~", "\\~\\~")
                .replace(">", "\u180E>");
    }

}
