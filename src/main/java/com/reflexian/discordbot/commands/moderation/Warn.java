package com.reflexian.discordbot.commands.moderation;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.mysql.MySQL;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Warn extends Command {
    public Warn(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        Server server = Server.getServer(event.getGuild());
        Member member=null;

        if (args.length < 3) {
            EmbedBuilder help = new EmbedBuilder();
            help.setColor(new Color(189,55,55));
            help.setTitle("Help - Warn");
            help.setDescription("Sub Commands start with <@775250061504413727>");
            help.addField("Commands", "Warn **-** Give a member a warning.", false);
            help.addField("Description", "This command allows you to give members \"warnings\". Warnings should be used as a before mute or ban.  At 3 warnings the member will be perm banned.", false);
            help.addField("Permission", "Requires ``KICK_MEMBERS`` to execute subcommands.", false);
            help.addField("Example", "```@Infinity#9833 warn @Raymond#0001 Being Toxic```", false);
            help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
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


            if (member.hasPermission(Permission.KICK_MEMBERS)) {
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Member with higher permissions.").setDescription("The user you want to warn has administrator permissions.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }
            if (!event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No permission.").setDescription("You need ``KICK_MEMBERS`` permission to use this command!").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }

            StringBuilder reason = new StringBuilder();
            try {
                if (!args[3].isEmpty()) {
                    for (String string : args) {
                        if (string.equals(args[0])||string.equals(args[1])||string.equals(args[2])) {
                            continue;
                        }
                        reason.append(string.toString().replace("/", "")).append(" ");
                    }
                }
                reason.toString().replaceAll("\\s+$", "");
            } catch (IndexOutOfBoundsException e) {
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No reason specified.").setDescription("You must include a reason to warn.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }

            ResultSet rs = Main.getPlugin().executeQuery("SELECT * FROM user_data WHERE user_key = '"+member.getId()+"#"+event.getGuild().getId()+"';", true);
            if (!rs.next()) MySQL.createMember(member, event.getGuild());

            String rea = reason.toString().replaceAll("\\s+$", "");

            StringBuilder warnings = new StringBuilder();
            String[] ar = rs.getString("warnings_reasons").split("/");
            if (ar[0].equalsIgnoreCase("none")) {
                ar[0]=reason.toString().replaceAll("\\s+$", "");
            } else if (ar[1].equalsIgnoreCase("none")) {
                ar[1]=reason.toString().replaceAll("\\s+$", "");
            }else if (ar[2].equalsIgnoreCase("none")) {
                ar[2]=reason.toString().replaceAll("\\s+$", "");
            }
            warnings.append(ar[0]).append("/").append(ar[1]).append("/").append(ar[2]);
            System.out.println(warnings.toString());

            EmbedBuilder warnlog = new EmbedBuilder().setTitle("\ud83d\udee1\ufe0f Warned " + member.getUser().getAsTag()).setColor(new Color(167, 76, 76)).setDescription(member.getUser().getAsTag() + " has been warned by " + event.getAuthor().getAsMention()+".").addField("Reason", "```" + rea + "```", false).setTimestamp(new Date().toInstant()).setFooter("Executed by " + event.getAuthor().getAsTag());
            if (server.getSettings().isLogging_enabled() && event.getGuild().getTextChannelById(server.getSettings().getLogging_channel())!=null) {
                sendMessage(event.getGuild().getTextChannelById(server.getSettings().getLogging_channel()), warnlog.build(), null);
            }

            Statement s = Main.getPlugin().getConnection().createStatement();
            System.out.println(rs.getLong("warnings")+"");
            s.execute("UPDATE user_data SET warnings=" + (rs.getLong("warnings")+1)+" WHERE user_key='" + member.getId() + "#" + event.getGuild().getId()+"';");
            s.execute("UPDATE user_data SET warnings_reasons='" + warnings.toString()+"' WHERE user_key='" + member.getId() + "#" + event.getGuild().getId()+"';");

            EmbedBuilder warning = new EmbedBuilder();
            warning.setColor(new Color(60, 191, 90));
            warning.setTitle("Successfully Warned " + member.getUser().getAsTag());
            warning.addField("Reason", "```" + rea + "```", false);
            warning.addField("Punisher", event.getAuthor().getAsTag(), false);
            warning.addField("Total Warnings", (rs.getLong("warnings")+1)+ "/3", false);
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


            if (rs.getLong("warnings")+1 >= 3) {

                EmbedBuilder banned = new EmbedBuilder();
                banned.setColor(new Color(167, 76, 76));
                banned.setTitle("Successfully banned " + member.getUser().getAsTag());
                banned.setDescription(member.getUser().getAsTag() + " has been banned.");
                banned.addField("Duration", "Permanent", false);
                banned.addField("Punisher", event.getAuthor().getAsTag(), false);
                banned.addField("Reason", "```"+"Reaching 3 warnings:\n1. "+ ar[0]+"\n2. "+ar[1]+"\n3. " + ar[2] + "```", false);
                banned.setFooter("Executed on " + new Date(), event.getAuthor().getAvatarUrl());
                banned.setTimestamp(new Date().toInstant());

                EmbedBuilder banlog = new EmbedBuilder().setTitle("\ud83d\udea8 Banned " + member.getUser().getAsTag()).setColor(new Color(167, 76, 76)).setDescription(member.getUser().getAsTag() + " has been banned by " + event.getAuthor().getAsMention()+".").addField("Reason", "```"+"Reaching 3 warnings;\n1. "+ ar[0]+"\n2. "+ar[1]+"\n3. " + "```", false).addField("Duration", "Permanent", false).setTimestamp(new Date().toInstant()).setFooter("Executed by " + event.getAuthor().getAsTag());
                if (server.getSettings().isLogging_enabled() && event.getGuild().getTextChannelById(server.getSettings().getLogging_channel())!=null) {
                    sendMessage(event.getGuild().getTextChannelById(server.getSettings().getLogging_channel()), banlog.build(), null);
                }

                Main.logger.warn(event.getAuthor().getAsTag() + " has banned " + member.getUser().getAsTag() + " permanently from " + event.getGuild().getName() + " for \"" + warnings.toString()+"\"");
                sendMessage(event.getTextChannel(), banned.build(), 30);

                member.getUser().openPrivateChannel().queue((channel2) -> {
                    channel2.sendMessage(banned.setTitle("You were banned from " + event.getGuild().getName()).setDescription("You are banned permanently. This action is non reversible!").build()).queue();
                });
                event.getGuild().ban(member, 0, warnings.toString()).queueAfter(5, TimeUnit.SECONDS);
                return;
            }

            long warningCount = rs.getLong("warnings");


            Member finalMember = member;
            member.getUser().openPrivateChannel().queue((channel2) -> {
                EmbedBuilder warned = new EmbedBuilder();
                warned.setColor(new Color(141, 44, 44));
                warned.setTitle("You were warned in " + event.getGuild().getName() +"!");
                warned.setDescription("You were warned for... ```" + rea + "```You will be banned when you reach 3 warnings.");
                warned.addField("Total Warnings", warningCount+"/3",false);
                warned.setFooter("Sent to " + finalMember.getUser().getAsTag(), finalMember.getUser().getAvatarUrl()).setTimestamp(new Date().toInstant());
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
