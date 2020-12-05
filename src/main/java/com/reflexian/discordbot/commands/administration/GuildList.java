package com.reflexian.discordbot.commands.administration;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class GuildList extends Command {
    public GuildList(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException, InsufficientPermissionException {
        event.getChannel().sendMessage("This may take a few moments...").queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
        EmbedBuilder em = new EmbedBuilder();
        em.setColor(new Color(72, 173, 241));
        em.setTitle("Guild List");
        em.setDescription("This is the list of guilds currently using the bot.");
        for (Guild guild : Main.getJda().getGuilds()) {
            String invite;
            try {
                //invite = guild.getChannels().get(1).createInvite().setMaxAge(3600).complete().getUrl();
                invite = "null";
            } catch (InsufficientPermissionException e) {
                invite = "null";
            }
            em.addField("["+guild.getName()+"]("+invite+")", guild.getOwner().getUser().getAsTag() + " | " + guild.getMemberCount() + " | " + guild.getId(), false);
        }
        em.setFooter("Only executable by bot administrators.");
        event.getChannel().sendMessage(em.build()).queue();
    }

    @Override
    public void cancel() {

    }
}
