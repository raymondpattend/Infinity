package com.reflexian.discordbot.commands.leveling;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Leaderboard extends Command {

    public Leaderboard(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {

        if (!MySQL.getBool("guild_data","level_enabled", "guild_id", event.getGuild().getId())) {
            EmbedBuilder em = new EmbedBuilder();
            em.setColor(new Color(151, 30, 30));
            em.setTitle("Disabled.");
            em.setDescription("Leveling is disabled in this guild.");
            em.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            event.getChannel().sendMessage(em.build()).queue(message -> {
                message.delete().queueAfter(10, TimeUnit.SECONDS);
            }, (failure) -> {
                Main.logger.warn("Failed to execute command \"leaderboard\" in " + event.getGuild().getName() + " because of " + failure.getLocalizedMessage()+".");
            });
            return;
        }

        EmbedBuilder em = new EmbedBuilder();
        em.setColor(new Color(43, 71, 106));
        em.setTitle("Level Leaderboard for " + event.getGuild().getName()+".");
        em.setDescription("This is the level leaderboard for " +event.getGuild().getName()+ ".\nYou can check your level using ```@Infinity#9388 rank```");
        PreparedStatement ps = Main.getPlugin().getConnection()
                .prepareStatement("SELECT * FROM user_data WHERE user_key LIKE '%" + event.getGuild().getIdLong() + "%' ORDER BY leveling_level DESC, leveling_xp DESC;");
        ResultSet rs = ps.executeQuery();
        int i = 0;
        while(rs.next()&&i<10) {
            i++;
            User user = event.getJDA().getUserById(rs.getString("user_key").split("#")[0]);
            em.addField("#" + i + ". " +user.getAsTag(), "Level " + rs.getLong("leveling_level") + " XP " + rs.getLong("leveling_xp")+"/"+rs.getLong("leveling_xpneeded"), false);
        }
        em.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
        event.getChannel().sendMessage(em.build()).queue(message -> {
            if (message == null) return;
            message.delete().queueAfter(30, TimeUnit.SECONDS);
        }, (failure) -> {
            Main.logger.warn("Failed to execute command \"leaderboard\" in " + event.getGuild().getName() + " because of " + failure.getLocalizedMessage()+".");
        });
    }

    @Override
    public void cancel() {

    }
}
