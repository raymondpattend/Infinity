package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;

public class BanList extends Command {


    public BanList(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            EmbedBuilder em = new EmbedBuilder();
            em.setTitle("No Permission").setDescription("You need ``BAN_MEMBERS`` permission to use this command!").setColor(new Color(213, 58, 58)).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            sendMessage(event.getTextChannel(), em.build(), 15);
            return;
        }
        EmbedBuilder em = new EmbedBuilder();
        em.setColor(new Color(34, 97, 146));
        em.setTitle("Ban List for " + event.getGuild().getName());
        em.setDescription("This is the list of banned players\n``Username:ID``");
        TextChannel textChannel = event.getTextChannel();
        event.getGuild().retrieveBanList().queue(bans -> {
            if (textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.BAN_MEMBERS)) {
                if (bans.size()==0) {
                    em.addField("No Bans", "This server has no bans :D", false);
                }
                for(Guild.Ban ban : bans) {
                    em.addField(ban.getUser().getAsTag()+":"+ban.getUser().getIdLong(), (ban.getReason()==null ? "No Reason Provided" : ban.getReason()) , false);
                }
                sendMessage(textChannel, em.build(), 60);
            } else {
                sendMessage(textChannel, "I do not have permission to view the ban list :(", 15);
            }
        });
    }

    @Override
    public void cancel() {

    }
}
