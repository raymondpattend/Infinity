package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class Kick extends Command {
    public Kick(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {

    }

    @Override
    public void cancel() {

    }
}
