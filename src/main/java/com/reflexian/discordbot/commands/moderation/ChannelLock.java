package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChannelLock extends Command {
    public ChannelLock(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    List<TextChannel> lockedTextChannels = new ArrayList<>();

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        Server server = Server.getServer(event.getGuild());
    }

    @Override
    public void cancel() {

    }
}
