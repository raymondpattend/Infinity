package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.commands.fun.Embed;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Warn extends Command {
    public Warn(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        Member member=null;

        if (args.length < 3) {
            event.getChannel().sendMessage(new EmbedBuilder().setTitle("Not enough arguments.").setDescription("Correct usage:\n``@Infinity#9833 warn <mention / id> <reason>``").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
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
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Member with higher permissions.").setDescription("The user you want to warn has administrator permissions.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }
            if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No permission.").setDescription("You need ``BAN_MEMBERS`` permission to use this command!").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }

            StringBuilder reason = new StringBuilder();
            try {
                if (!args[3].isEmpty()) {
                    for (String string : args) {
                        if (string.equals(args[0])||string.equals(args[1])||string.equals(args[2])) {
                            continue;
                        }
                        reason.append(string).append(" ");
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No reason specified.").setDescription("You must include a reason to warn.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }



            EmbedBuilder warning = new EmbedBuilder();
            warning.setColor(new Color(60, 191, 90));
            warning.setTitle("Successfully Warned " + member.getUser().getAsTag());
            warning.addField("Reason", "```" + reason + "```", false);
            warning.addField("Punisher", event.getAuthor().getAsTag(), false);
            //warning.addField("Total Warnings", i + "/3", false);
            warning.setFooter("Executed on " + new Date(), event.getAuthor().getAvatarUrl());

            // @Infinity#9388 ban <user> <reason...>
            /*if (i>=3) {
                StringBuilder historyBuilder = new StringBuilder();
                for (String string : warningHistory) {
                    historyBuilder.append(string+" ");
                }
                String[] banArgs = { "@Infinity#9388", "ban", member.getId(), historyBuilder.toString() };
                System.out.println(historyBuilder.toString());
                Ban ban = new Ban(banArgs, event.getMember(), event.getAuthor());
                ban.execute(event);
                ban=null;
            }*/

            sendMessage(event.getTextChannel(), warning.build(), 30);

            //int finalI = i;
            member.getUser().openPrivateChannel().queue((channel2) -> {
                EmbedBuilder warned = new EmbedBuilder();
                warned.setColor(new Color(141, 44, 44));
                warned.setTitle("You were warned in " + event.getGuild().getName() +"!");
                warned.setDescription("You were warned for " + reason + ". Don't let it happen again or you may get banned.");
                //warned.addField("Total Warnings", finalI +"/3",false);
                channel2.sendMessage(warned.build()).queue();
            });
        }catch (NullPointerException | NumberFormatException e) {
            sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Not a valid member.").setDescription("You must include a valid member or mention.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
        }
    }

    @Override
    public void cancel() {

    }
}
