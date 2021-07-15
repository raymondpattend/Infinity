package com.reflexian.discordbot.commands.utilities;

import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;

public class Privacy extends Command {


    public Privacy(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        EmbedBuilder em = new EmbedBuilder().setTitle("Privacy - " + event.getAuthor().getAsTag()).setDescription("This is how we use your Data to provide a better experience.");
        em.addField("Data we store.", "We store data in order to provide an amazing experience for you. Data types we store:\n- List of the guilds and IDs in which the bot is in\n- List of all user IDs in which the bot is in\n- Settings for your guild (Infinity)\n- Leveling (simply 3 numbers)\n- Some (user inputted) Guild Role IDs", false);
        em.addField("Why do we store this data?", "We store the data above because it is a full requirement for Infinity. The data we store is used to save settings to each individuel guild (for a better user experience). The data we store is NEVER seen by or sent to 3rd parties / non authorized bot developers. You can feel safe knowing your data is safe.", false);
        em.addField("How long is my user/guild data stored?", "We store the data above for aslong as the bot is in a guild. Once the bot leaves a guild, all data (including user data) is deleted instantly. Opon rejoining a guild, the settings will be the default (because we deleted their settings eariler).", false);
        em.addField("How can I get my data deleted?", "If you would like your data deleted from our servers, please join our [Support Server](https://discord.gg/WpCpRbC) or email ``raymond@reflexian`` with your Discord ID.", false);
        em.setColor(new Color(40, 127, 161));
        em.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
        em.setThumbnail(event.getGuild().getIconUrl());
        sendMessage(event.getTextChannel(), em.build(), 60);
    }

    @Override
    public void cancel() {

    }
}
