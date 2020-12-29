package com.reflexian.discordbot.commands.administrative;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;

public class Log extends Command {
    public Log(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    private final EmbedBuilder help = new EmbedBuilder().setTitle("Help - Log").setDescription("Sub Commands start with <@775250061504413727> log").addField("Commands", "Enable/Disable **-** Enable/Disable logging in this guild\nSetChannel **-** Set the channel in which log messages are sent\nGetChannel **-** Get the channel in which log messages are sent", false).addField("Description", "This feature allows you to log all chat related actions in a channel.", false).addField("Permission", "Requires ``MANAGE_SERVER`` to execute subcommands.", false).addField("Example", "```@Infinity#9388 log enable```", false).setColor(new Color(189, 55, 55));

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        Server server = Server.getServer(event.getGuild());
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No permission.").setDescription("You need ``MANAGE_SERVER`` permission to use this command!").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
            return;
        }
        if (args.length < 3) {
            sendMessage(event.getTextChannel(), help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).build(), 60);
            return;
        }
        if (args.length == 3) {
            switch (args[2].toLowerCase()) {
                case "enable":
                    if (!server.getSettings().isLogging_enabled()) {
                        server.getSettings().setLogging_enabled(true);
                        sendMessage(event.getTextChannel(), this.valueSet("Logging Enabled", true, false).build(), 60);
                    } else {
                        sendMessage(event.getTextChannel(), this.valueSet("Logging Enabled", true, true).build(), 60);
                    }
                    return;
                case "disable":
                    if (server.getSettings().isLogging_enabled()) {
                        server.getSettings().setLogging_enabled(false);
                        sendMessage(event.getTextChannel(), this.valueSet("Logging Enabled", false, false).build(), 60);
                    } else {
                        sendMessage(event.getTextChannel(), this.valueSet("Logging Enabled", false, true).build(), 60);
                    }
                    return;
                case "getchannel":
                    TextChannel textChannel = server.getSettings().getLogging_channel() == 0 ? null : event.getGuild().getTextChannelById(server.getSettings().getLogging_channel());
                    EmbedBuilder value = new EmbedBuilder().setTitle("Current Logging Channel").setDescription("" + (textChannel == null ? "No valid channel has been set!" : textChannel.getAsMention())).setColor(new Color(36, 70, 128)).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), value.build(), 60);
                    return;
                default:
                    sendMessage(event.getTextChannel(), help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).build(), 60);
                    return;
            }
        } else if (args.length == 4) {
            switch (args[2].toLowerCase()) {
                case "setchannel":
                    if (event.getMessage().getMentionedChannels().size() == 0) {
                        EmbedBuilder failed = new EmbedBuilder().setColor(new Color(185, 55, 55)).setTitle("Invalid Channel").setDescription("You did not include a valid channel mention.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                        sendMessage(event.getTextChannel(), failed.build(), 40);
                        return;
                    }
                    server.getSettings().setLogging_channel(event.getMessage().getMentionedChannels().get(0).getIdLong());
                    sendMessage(event.getTextChannel(), this.valueSet("Logging Channel",  event.getMessage().getMentionedChannels().get(0).getName(), false).build(), 60);
                    return;
                default:
                    sendMessage(event.getTextChannel(), help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).build(), 60);
                    return;
            }
        }
    }

    @Override
    public void cancel() {}

    private EmbedBuilder errorEmbed = new EmbedBuilder().setTitle("Something went wrong :(").setDescription("Sorry! Something went wrong on my end and the developers have been notified! Expect a fix within 12 hours.").setColor(new Color(144, 39, 39));

    private EmbedBuilder valueSet(String value, boolean valueboolean, boolean failed) {
        EmbedBuilder em = new EmbedBuilder();

        if (failed) {
            em.setColor(new Color(165, 48, 48));
            em.setTitle("Failed to update value");
            em.setDescription("The value \"" + value+"\" is already " + valueboolean+"!");
        } else {
            em.setColor(new Color(29, 120, 12));
            em.setTitle("Updated Value - " + value);
            em.setDescription("The value \"" + value+"\" has been updated to " + valueboolean+".");
        }
        return em;
    }

    private EmbedBuilder valueSet(String value, String valuestring, boolean failed) {
        EmbedBuilder em = new EmbedBuilder();

        if (failed) {
            em.setColor(new Color(165, 48, 48));
            em.setTitle("Failed to update value");
            em.setDescription("The value \"" + value+"\" is already \"" + valuestring+"\"!");
        } else {
            em.setColor(new Color(29, 120, 12));
            em.setTitle("Updated Value - " + value);
            em.setDescription("The value \"" + value+"\" has been updated to... \"" + valuestring +"\".");
        }
        return em;
    }
}
