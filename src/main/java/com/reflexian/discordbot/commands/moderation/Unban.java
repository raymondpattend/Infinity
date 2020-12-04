package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

public class Unban extends Command {

    public Unban(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
    }

    @Override
    public void cancel() {

    }
}
