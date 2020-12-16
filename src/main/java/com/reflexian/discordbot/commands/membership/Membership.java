package com.reflexian.discordbot.commands.membership;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Membership extends Command {


    public Membership(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {

        PreparedStatement ps = Main.getPlugin().getConnection()
                .prepareStatement("SELECT * FROM memberships WHERE guild_id='" + event.getGuild().getId()+"';");
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            EmbedBuilder membership = new EmbedBuilder();
            membership.setColor(new Color(61, 126, 182));
            membership.setTitle("<:verified2:785197016522555394> Membership details for " + event.getGuild().getName());
            membership.setThumbnail(event.getGuild().getIconUrl());
            membership.setDescription("Memberships are paid subscriptions which adds additional skills to Infinity's already diverse skillset.");
            membership.addField("Guild ID", event.getGuild().getId(), false);
            membership.addField("Membership ID", rs.getString("membership_id"), false);
            membership.addField("Description", rs.getString("description"), false);
            membership.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            sendMessage(event.getTextChannel(), membership.build(), 30);
            return;
        }
        EmbedBuilder em = new EmbedBuilder().setTitle("Invalid Membership.").setDescription("You can purchase a Infinity Membership [here](https://www.reflexian.com/?post_type=product&p=2878&preview=true)").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55));
        sendMessage(event.getTextChannel(), em.build(), 30);
    }

    @Override
    public void cancel() {

    }
}
