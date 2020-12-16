package com.reflexian.discordbot.commands.utilities;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.mysql.MySQL;
import com.reflexian.discordbot.utilities.UtilStrings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class User extends Command {
    public User(String[] command, @Nullable Member member, net.dv8tion.jda.api.entities.@Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws NullPointerException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
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

            PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                    .prepareStatement("SELECT * FROM user_data WHERE user_key = '"+ member.getId()+"#"+event.getGuild().getId()+"';");
            ResultSet rs = preparedStatement.executeQuery();
            long xp = 0,level=0,maxXp=0;
            if (rs.next()) {
                xp = rs.getInt("leveling_xp");
                level = rs.getLong("leveling_level");
                maxXp = rs.getLong("leveling_xpneeded");
            } else {
                xp = 0;
                level = 0;
                maxXp = 100;
            }
            EmbedBuilder player = new EmbedBuilder();
            player.setColor(new Color(60, 134, 191));
            player.setThumbnail(user.getAvatarUrl());
            player.setTitle("User Information: " + member.getUser().getAsTag());
            player.setDescription("Joined this guild on ``" + UtilStrings.formatOffsetDateTime(member.getTimeJoined()) + "``\nJoin Discord on ``" + UtilStrings.formatOffsetDateTime(user.getTimeCreated()) + "``");
            player.addField("Identity", "**Username** - " + user.getAsTag()+"\n**ID** - " + user.getId()+"\n**Nickname** - " + (member.getNickname() == null ? "N/A" : member.getEffectiveName()), false);
            player.addField("Leveling", "**XP** - (" + xp+"/"+maxXp+")\n**Level** - (" + level+")", false);
            try {
                player.addField("Status", member.getActivities().get(0) == null ? "N/A" : member.getActivities().get(0).getName(), false);
            }catch (IndexOutOfBoundsException e) {
                player.addField("Status", "N/A", false);
            }
            player.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            sendMessage(event.getTextChannel(), player.build(), 30);
        }catch (NullPointerException | NumberFormatException | SQLException e) {
            sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Not a valid user.").setDescription("You must include a valid id or mention.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 30);
        }



    }

    @Override
    public void cancel() {

    }
}
