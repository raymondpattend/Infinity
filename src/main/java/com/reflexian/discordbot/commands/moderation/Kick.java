package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Kick extends Command {
    public Kick(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        Server server = Server.getServer(event.getGuild());

        Member member=null;

        if (args.length < 3) {

            EmbedBuilder help = new EmbedBuilder();
            help.setColor(new Color(189,55,55));
            help.setTitle("Help - Kick");
            help.setDescription("Sub Commands start with <@775250061504413727>");
            help.addField("Commands", "Kick **-** Kick / Remove a member from this server", false);
            help.addField("Description", "This command allows you to kick a user from this guild. They will still be able to join using a valid invite link.", false);
            help.addField("Permission", "Requires ``KICK_MEMBERS`` to execute subcommands.", false);
            help.addField("Example", "```@Infinity#9833 kick @Raymond#0001 Advertising External Links", false);
            help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setTimestamp(new Date().toInstant());
            sendMessage(event.getTextChannel(), help.build(), 40);
            return;
        }


        try {
            if (event.getMessage().getMentionedUsers().size() == 2) {
                if (args[2].startsWith("<@") && args[2].endsWith(">")) {
                    member = event.getGuild().getMemberById(args[2].replace("!", "").replace("@", "").replace(">", "").replace("<", ""));
                }
            } else {
                member = event.getGuild().getMemberById(args[2]);
            }

            if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No permission.").setDescription("You need ``KICK_MEMBERS`` permission to use this command!").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }
            if (member.hasPermission(Permission.KICK_MEMBERS)) {
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Member with higher permissions.").setDescription("The user you want to punish has administrator permissions.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }

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
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No reason specified.").setDescription("You must include a reason to punish.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }

            EmbedBuilder kicked = new EmbedBuilder();
            kicked.setColor(new Color(167, 76, 76));
            kicked.setTitle("Successfully kicked " + member.getUser().getAsTag());
            kicked.setDescription("Kicked " + member.getUser().getAsTag() + ". They can rejoin using a valid invite.");
            kicked.addField("Punisher", event.getAuthor().getAsTag(), false);
            kicked.addField("Reason", "```" + str + "```", false);
            kicked.setFooter("Executed on " + new Date(), event.getAuthor().getAvatarUrl());
            kicked.setTimestamp(new Date().toInstant());

            EmbedBuilder kicklog = new EmbedBuilder().setTitle("\ud83e\udd7e Kicked " + member.getUser().getAsTag()).setColor(new Color(167, 76, 76)).setDescription(member.getUser().getAsTag() + " has been kicked by " + event.getAuthor().getAsMention()+".").addField("Reason", "```" + str + "```", false).setTimestamp(new Date().toInstant()).setFooter("Executed by " + event.getAuthor().getAsTag());
            if (server.getSettings().isLogging_enabled() && event.getGuild().getTextChannelById(server.getSettings().getLogging_channel())!=null) {
                sendMessage(event.getGuild().getTextChannelById(server.getSettings().getLogging_channel()), kicklog.build(), null);
            }

            Main.logger.warn(event.getAuthor().getAsTag() + " has kicked " + member.getUser().getAsTag() + " from " + event.getGuild().getName() + " for \"" + str+"\"");
            sendMessage(event.getTextChannel(), kicked.build(), 30);

            member.getUser().openPrivateChannel().queue((channel2) -> {
                channel2.sendMessage(kicked.setTitle("You were kicked from " + event.getGuild().getName() + "!").setDescription("You are no longer in " + event.getGuild().getName() + ". You can rejoin using a valid invite link.").build()).queue();
            });
            event.getGuild().kick(member, str.toString()).queue();

        }catch (NullPointerException | NumberFormatException e) {
            sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Not a valid member.").setDescription("You must include a valid member or mention.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
        }
    }

    @Override
    public void cancel() {

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
}
