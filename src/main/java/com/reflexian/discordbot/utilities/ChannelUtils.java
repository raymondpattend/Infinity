package com.reflexian.discordbot.utilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class ChannelUtils {

    public static TextChannel getOpenChannel(Guild guild) {
        for (TextChannel textChannel : guild.getTextChannels()) {
            if (textChannel.canTalk()) {
                return textChannel;
            }
         }
        return null;
    }

}
