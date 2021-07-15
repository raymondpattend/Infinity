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
import java.util.Date;

public class Unwarn extends Command {
    public Unwarn(String[] command, @Nullable Member member, @Nullable User user) {
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
            help.setTitle("Help - Unwarn");
            help.setDescription("Sub Commands start with <@775250061504413727>");
            help.addField("Commands", "Unwarn **-** Remove ``ALL`` warnings from a member", false);
            help.addField("Description", "This feature allows you to remove every warning a member has.", false);
            help.addField("Permission", "Requires ``BAN_MEMBERS`` to execute subcommands.", false);
            help.addField("Example", "```@Infinity#9833 warnings @Raymond#0001```", false);
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

            if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No permission.").setDescription("You need ``BAN_MEMBERS`` permission to use this command!").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
                return;
            }

            ResultSet rs = Main.getPlugin().executeQuery("SELECT * FROM user_data WHERE user_key = '"+event.getAuthor().getId()+"#"+event.getGuild().getId()+"';", true);
            if (!rs.next()) MySQL.createMember(event.getMember(), event.getGuild());

            if (rs.getLong("warnings")==0) {
                EmbedBuilder none=new EmbedBuilder();
                none.setTitle("Warnings for " + member.getUser().getAsTag());
                none.setThumbnail(member.getUser().getAvatarUrl()).setDescription("These are the warnings for " + member.getUser().getAsTag()+".");
                none.addField("No warnings", member.getUser().getAsTag() + " has no warnings!", false).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setTimestamp(new Date().toInstant()).setColor(new Color(45, 130, 77));
                sendMessage(event.getTextChannel(), none.build(), 60);
                return;
            }
            EmbedBuilder warnings=new EmbedBuilder();

            Statement s = Main.getPlugin().getConnection().createStatement();
            s.execute("UPDATE user_data SET warnings=" +0+" WHERE user_key='" + member.getId() + "#" + event.getGuild().getId()+"';");
            s.execute("UPDATE user_data SET warnings_reasons='" + "none/none/none"+"' WHERE user_key='" + member.getId() + "#" + event.getGuild().getId()+"';");

            warnings.setTitle("Removed all warnings for " + member.getUser().getAsTag());
            warnings.setThumbnail(member.getUser().getAvatarUrl()).setDescription(member.getUser().getAsTag() + " now has no warnings!");
            warnings.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setTimestamp(new Date().toInstant()).setColor(new Color(45, 130, 77));
            warnings.setTimestamp(new Date().toInstant());
            sendMessage(event.getTextChannel(), warnings.build(), 60);
        }catch (NullPointerException | NumberFormatException e) {
            sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Not a valid member.").setDescription("You must include a valid member or mention.").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
        }
    }

    @Override
    public void cancel() {

    }
}
