package com.reflexian.discordbot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public abstract class Command extends ListenerAdapter {

    private final User user;
    private final Member member;
    private final String[] command;

    private boolean isCancelled;

    private String description = null;
    private int permissionID = 0;

    public Command(String[] command, @Nullable Member member, @Nullable User user) {
        this.command = command;
        this.member = member;
        this.user = user;
        this.isCancelled = false;
    }

    public abstract void execute(MessageReceivedEvent event) throws SQLException;
    public abstract void cancel();

    public User getUser() {
        return user;
    }
    public Member getMember() {
        return member;
    }
    public String getDescription() {
        return description;
    }
    public int getPermissionID() {
        return permissionID;
    }
    public String[] getCommandArgs() {
        return command;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPermissionID(int permissionID) {
        this.permissionID = permissionID;
    }

    public void sendMessage(TextChannel textChannel, MessageEmbed embed, @Nullable Integer secondDelete) {
        if (textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE)&&textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.VIEW_CHANNEL)) {
            textChannel.sendMessage(embed).queue(message -> {
                if (secondDelete==null) return;
                if (message == null) return;
                message.delete().queueAfter(secondDelete, TimeUnit.SECONDS);
            });
        }
    }

    public void sendMessage(TextChannel textChannel, String text, @Nullable Integer secondDelete) {
        if (textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE)&&textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.VIEW_CHANNEL)) {
            textChannel.sendMessage(text).queue(message -> {
                if (secondDelete==null) return;
                if (message==null) return;
                message.delete().queueAfter(secondDelete, TimeUnit.SECONDS);
            });
        }
    }

    public boolean isCancelled() {
        return isCancelled;
    }
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
