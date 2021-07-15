package com.reflexian.discordbot.commands.games;

import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.util.Date;

public class Counting extends Command {
    public Counting(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        Server server = Server.getServer(event.getGuild());
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No permission.").setDescription("You need ``MANAGE_SERVER`` permission to use this command!\nTrying to get your level? Use ``@Infinity#9388 rank``").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
            return;
        }
        if (args.length < 3) {
            EmbedBuilder help = new EmbedBuilder().setColor(new Color(189, 55, 55));
            help.setTitle("Help - Counting");
            help.setDescription("Sub Commands start with <@775250061504413727> counting");
            help.addField("Commands", "Enable/Disable **-** Enable/Disable counting in this guild\nSetChannel **-** Set the channel for counting\nGetChannel **-** Get the channel for counting\n", false);
            help.addField("Description", "Leveling Commands allow you to change how leveling works in your guild. You are able to change the levelup message, enable/disable leveling, and more.", false);
            help.addField("Permission", "Requires ``MANAGE_SERVER`` to execute subcommands.", false);
            help.addField("Example", "```@Infinity#9388 counting setchannel #count-to-million```", false);
            help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            help.setTimestamp(new Date().toInstant());
            sendMessage(event.getTextChannel(), help.build(), 60);
            return;
        }
    }

    @Override
    public void cancel() {

    }
}
