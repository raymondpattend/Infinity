package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.util.*;

public class ChannelLock extends Command {
    public ChannelLock(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    public static Map<TextChannel, Role> lockedTextChannels = new HashMap<>();

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            EmbedBuilder nopermission = new EmbedBuilder().setColor(new Color(151, 78, 78)).setTitle("No Permission").setDescription("You need ``MANAGE_MESSAGES`` permission to use this command!").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setTimestamp(new Date().toInstant());
            sendMessage(event.getTextChannel(), nopermission.build(), 15);
            return;
        }

        if (args.length<3) {
            if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                EmbedBuilder nopermission = new EmbedBuilder().setColor(new Color(151, 78, 78)).setTitle("I don't have permission...").setDescription("I'm not able to modify channels. Please give me permission ``MANAGE_CHANNELS``.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setTimestamp(new Date().toInstant());
                sendMessage(event.getTextChannel(), nopermission.build(), 30);
                return;
            }
            this.member=event.getMember();
            if (lockedTextChannels.containsKey(event.getTextChannel())){
                unlockChannel(event.getTextChannel());
            }else{
                lockChannel(event.getTextChannel(), "No Reason Provided");
            }
            return;
        }
        TextChannel tx;
        if (event.getMessage().getMentionedChannels().size()==0) {
            // ARGS 2
            try {
                if (event.getGuild().getTextChannelById(args[2])!=null) {
                    tx=event.getGuild().getTextChannelById(args[2]);
                } else {
                    EmbedBuilder nopermission = new EmbedBuilder().setColor(new Color(151, 78, 78)).setTitle("Unknown Channel ID").setDescription("The ID you specified is not valid. Please specify a real text channel id or mention a text channel.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setTimestamp(new Date().toInstant());
                    sendMessage(event.getTextChannel(), nopermission.build(), 30);
                    return;
                }
            }catch (NumberFormatException e) {
                EmbedBuilder nopermission = new EmbedBuilder().setColor(new Color(151, 78, 78)).setTitle("Unknown Channel ID").setDescription("The ID you specified is not valid. Please specify a real text channel id or mention a text channel.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setTimestamp(new Date().toInstant());
                sendMessage(event.getTextChannel(), nopermission.build(), 30);
                return;
            }
        } else tx=event.getMessage().getMentionedChannels().get(0);

        StringBuilder str = new StringBuilder();
        try {
            if (!args[3].isEmpty()) {
                for (String string : args) {
                    if (string.equals(args[0])||string.equals(args[1])||string.equals(args[2])) {
                        continue;
                    }
                    str.append(string).append(" ");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            str.append("No Reason Provided");
        }

        this.member=event.getMember();
        if (lockedTextChannels.containsKey(tx)){
            unlockChannel(tx);
            EmbedBuilder success = new EmbedBuilder().setTitle("\ud83d\udd13 Successfully unlocked " + tx.getName()).setDescription(tx.getAsMention() + " has been unlocked.").setFooter("Unlocked by " + this.member.getUser().getAsTag(), this.member.getUser().getAvatarUrl()).setTimestamp(new Date().toInstant()).setColor(new Color(48, 139, 54));
            sendMessage(event.getTextChannel(), success.build(), null);
        }else{
            lockChannel(tx, str.toString());
            EmbedBuilder success = new EmbedBuilder().setTitle("\ud83d\udd12 Successfully locked " + tx.getName()).setDescription(tx.getAsMention() + " has been locked.").setFooter("Locked by " + this.member.getUser().getAsTag(), this.member.getUser().getAvatarUrl()).setTimestamp(new Date().toInstant()).setColor(new Color(48, 139, 54));
            sendMessage(event.getTextChannel(), success.build(), null);
        }
    }

    private void lockChannel(TextChannel textChannel, String reason) {

        EmbedBuilder locked = new EmbedBuilder().setTitle("\ud83d\udd12 Channel Locked").setDescription("This channel has been locked.").addField("Reason", "```" + reason + "```", false).setFooter("Channel locked by " + member.getUser().getAsTag(),member.getUser().getAvatarUrl()).setThumbnail("https://i.gyazo.com/dab3ddbaf7d89bcd6acdfc8ec9550d41.png").setColor(new Color(165, 55, 64));

        sendMessage(textChannel, locked.build(), null);

        Role muterole = null;

        for (PermissionOverride permissionOverride : textChannel.getRolePermissionOverrides()) {
            if (permissionOverride.getAllowed().contains(Permission.MESSAGE_MANAGE)) continue;

            if (permissionOverride.getDenied().contains(Permission.MESSAGE_WRITE)) muterole= permissionOverride.getRole();

            permissionOverride.getManager().setDeny(Permission.MESSAGE_WRITE).queue();
        }
        lockedTextChannels.put(textChannel,muterole);
        EmbedBuilder banlog = new EmbedBuilder().setTitle("\ud83d\udd12 Locked #" + textChannel.getName()).setColor(new Color(167, 76, 76)).setDescription("The channel " + textChannel.getAsMention() + " has been locked.").setTimestamp(new Date().toInstant()).setFooter("Channel locked by " + this.member.getUser().getAsTag(), this.member.getUser().getAvatarUrl());
        Server server = Server.getServer(textChannel.getGuild());
        if (server.getSettings().isLogging_enabled() && textChannel.getGuild().getTextChannelById(server.getSettings().getLogging_channel())!=null) {
            sendMessage(textChannel.getGuild().getTextChannelById(server.getSettings().getLogging_channel()), banlog.build(), null);
        }
    }

    private Member member;

    private void unlockChannel(TextChannel textChannel) {
        EmbedBuilder unlocked = new EmbedBuilder().setTitle("\ud83d\udd13 Channel Unlocked").setDescription("This channel has been unlocked, you can now talk.").setFooter("Channel unlocked by " + member.getUser().getAsTag(),member.getUser().getAvatarUrl()).setColor(new Color(45, 154, 45));
        sendMessage(textChannel, unlocked.build(), null);
        for (PermissionOverride permissionOverride : textChannel.getRolePermissionOverrides()) {
            if (permissionOverride.getAllowed().contains(Permission.MESSAGE_MANAGE)) continue;

            if (permissionOverride.getRole() == lockedTextChannels.get(textChannel)) continue;

            permissionOverride.getManager().setAllow(Permission.MESSAGE_WRITE).queue();
        }
        lockedTextChannels.remove(textChannel);
        EmbedBuilder banlog = new EmbedBuilder().setTitle("\ud83d\udd13 Unlocked #" + textChannel.getName()).setColor(new Color(167, 76, 76)).setDescription("The channel " + textChannel.getAsMention() + " has been unlocked.").setTimestamp(new Date().toInstant()).setFooter("Channel unlocked by " + this.member.getUser().getAsTag(), this.member.getUser().getAvatarUrl());
        Server server = Server.getServer(textChannel.getGuild());
        if (server.getSettings().isLogging_enabled() && textChannel.getGuild().getTextChannelById(server.getSettings().getLogging_channel())!=null) {
            sendMessage(textChannel.getGuild().getTextChannelById(server.getSettings().getLogging_channel()), banlog.build(), null);
        }
    }

    @Override
    public void cancel() {

    }
}
