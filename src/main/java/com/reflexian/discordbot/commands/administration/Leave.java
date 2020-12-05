package com.reflexian.discordbot.commands.administration;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Leave extends Command {


    public Leave(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args.length <3) {
            EmbedBuilder no = new EmbedBuilder();
            no.setColor(new Color(186, 48, 48));
            no.setTitle("Must include a server ID");
            no.setDescription("This command requires a server id that the bot is in.");
            event.getChannel().sendMessage(no.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        EmbedBuilder em = new EmbedBuilder();
        try {
            long id = Long.parseLong(args[2]);
            em.setColor(new Color(43, 167, 76));
            em.setTitle("Successfully left " + Main.getJda().getGuildById(id).getName()+"!");
            em.setDescription("Infinity has left that discord.");
            Main.getJda().getGuildById(id).leave().queue();
            event.getChannel().sendMessage(em.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
        }catch (NumberFormatException | NullPointerException e) {
            em.setColor(new Color(179, 64, 64));
            em.setTitle("No such discord with ID.");
            em.setDescription("I am not in such discord with that id.");
            event.getChannel().sendMessage(em.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
        }

    }

    @Override
    public void cancel() {

    }
}
