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

        StringBuilder st = new StringBuilder();
        st.append("These are the commands available to you! (PREFIX IS @Infinity#9833)\n\n**Help** - You are here\n**Info** - Information about the bot\n**Uptime** - Uptime information\n**Say** - Make me say something (use \"***<number>**\" to delay auto delete)\n**UserInfo** - Get information about a user\n");
        if (event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            st.append("**Ban** - Ban a member\n**Unban** - Unban a member (ID only)\n");
        }
        if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            st.append("**Config** - Change guild settings");
        }
        help.setDescription(st.toString());
        help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
        event.getChannel().sendMessage(help.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
    }

    @Override
    public void cancel() {

    }
}
