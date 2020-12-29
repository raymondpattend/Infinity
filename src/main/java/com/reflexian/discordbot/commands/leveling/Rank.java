package com.reflexian.discordbot.commands.leveling;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.mysql.MySQL;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Rank extends Command {


    public Rank(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        Server server = Server.getServer(event.getGuild());

        if (!server.getSettings().isLevel_enabled()) {
            EmbedBuilder em = new EmbedBuilder();
            em.setColor(new Color(151, 30, 30));
            em.setTitle("Disabled.");
            em.setDescription("Leveling is disabled in this guild.");
            em.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            sendMessage(event.getTextChannel(), em.build(), 15);
            return;
        }

        Member member = null;
        if (args.length <3) {
            member=event.getMember();
        }
        try {
            if (member!=event.getMember()) {
                if (event.getMessage().getMentionedUsers().size() == 2) {
                    if (args[2].startsWith("<@") && args[2].endsWith(">")) {
                        member = event.getGuild().getMemberById(args[2].replace("!", "").replace("@", "").replace(">", "").replace("<", ""));
                    }
                } else {
                    member = event.getGuild().getMemberById(args[2]);
                }
            }
            net.dv8tion.jda.api.entities.User user = member.getUser();
            if (user.isBot()) {
                sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Not a valid user.").setDescription("You must include a valid id or mention.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 15);
                return;
            }

            EmbedBuilder rank = new EmbedBuilder();
            rank.setTitle("Level for " + user.getAsTag());


            ResultSet rs = Main.getPlugin().executeQuery("SELECT * FROM user_data WHERE user_key = '"+event.getAuthor().getId()+"#"+event.getGuild().getId()+"';", true);

            if (!rs.next()) {
                MySQL.createMember(event.getMember(), event.getGuild());

                rank.setDescription("**Level** 0\n**Level Progress** 0/100\n**XP for Next Level** 100");
            } else {
                rank.setDescription("**Level** " + rs.getLong("leveling_level")+"\n**Level Progress** " + rs.getLong("leveling_xp") + "/" + rs.getLong("leveling_xpneeded")+"\n**XP for Next Level** " + (rs.getLong("leveling_xpneeded")-rs.getLong("leveling_xp")));
            }

            rank.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setTimestamp(new Date().toInstant());
            sendMessage(event.getTextChannel(), rank.build(), 15);
        }catch (NullPointerException | NumberFormatException e) {
            sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Not a valid user.").setDescription("You must include a valid id or mention.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
        }

    }

    @Override
    public void cancel() {}
}
