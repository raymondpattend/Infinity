package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Ban extends Command {

    public Ban(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        Member member=null;

        if (args.length < 3) {
            event.getChannel().sendMessage(new EmbedBuilder().setTitle("Not enough arguments.").setDescription("Correct usage:\n``@Infinity#9833 ban <mention / id> <reason>``").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
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


            if (member.hasPermission(Permission.BAN_MEMBERS)) {
                event.getChannel().sendMessage(new EmbedBuilder().setTitle("Member with higher permissions.").setDescription("The user you want to punish has administrator permissions.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
            }
            if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                event.getChannel().sendMessage(new EmbedBuilder().setTitle("No permission.").setDescription("You need ``BAN_MEMBERS`` permission to use this command!").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
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
                event.getChannel().sendMessage(new EmbedBuilder().setTitle("No reason specified.").setDescription("You must include a reason to punish.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
            }

            EmbedBuilder banned = new EmbedBuilder();
            banned.setColor(new Color(60, 191, 90));
            banned.setTitle("Successfully banned " + member.getUser().getAsTag());
            banned.setDescription("Banned " + member.getUser().getAsTag() + " permanently. This action is non reversible!");
            banned.addField("Duration", "Permanent", false);
            banned.addField("Punisher", event.getAuthor().getAsTag(), false);
            banned.addField("Reason", "```" + str + "```", false);
            banned.setFooter("Executed on " + new Date(), event.getAuthor().getAvatarUrl());

            Main.logger.warn(event.getAuthor().getAsTag() + " has banned " + member.getUser().getAsTag() + " permanently from " + event.getGuild().getName() + " for \"" + str+"\"");
            if (event.getGuild().getId().equals("770142850633433094")) event.getGuild().getTextChannelsByName("actions", true).get(0).sendMessage(banned.build()).queue();

            event.getGuild().ban(member, 0).queue();

            event.getChannel().sendMessage(banned.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));

            member.getUser().openPrivateChannel().queue((channel2) -> {
                channel2.sendMessage(banned.setTitle("You were banned from " + event.getGuild().getName() + "!").setDescription("You are banned permanently. This action is non reversible!").build()).queue();
            });

        }catch (NullPointerException | NumberFormatException e) {
            event.getChannel().sendMessage(new EmbedBuilder().setTitle("Not a valid member.").setDescription("You must include a valid member or mention.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }





// EmbedBuilder em = new EmbedBuilder().setTitle("Member with higher permissions.").setDescription("The user you want to punish has administrator permissions.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
//EmbedBuilder em = new EmbedBuilder().setTitle("Not a valid member.").setDescription("You must include a valid member or mention.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
    }

    @Override
    public void cancel() {

    }
}
