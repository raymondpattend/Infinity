package com.reflexian.discordbot.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public abstract class Command {

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

    public boolean isCancelled() {
        return isCancelled;
    }
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
