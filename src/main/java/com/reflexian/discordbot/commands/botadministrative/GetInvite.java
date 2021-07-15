package com.reflexian.discordbot.commands.botadministrative;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.utilities.ChannelUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class GetInvite extends Command {
    public GetInvite(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    private String invite=null;
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
            TextChannel textChannel = ChannelUtils.getOpenChannel(Main.getJda().getGuildById(id));
            if (textChannel==null) {
                event.getChannel().sendMessage("That guild doesn't have any channels I can talk in :(").queue();
                return;
            }
            textChannel.createInvite().setMaxAge(300).queue(invite1 -> {
                invite = invite1.getUrl();
                em.setTitle("Successfully created an invite for " + Main.getJda().getGuildById(id).getName()+"!");
                em.setDescription("[Click Here]("+invite+")");
                event.getChannel().sendMessage(em.build()).queue();
            }, (failure) -> {
                em.setTitle("Failed to create invite.");
                em.setDescription("```" + failure.getLocalizedMessage()+"```");
                event.getChannel().sendMessage(em.build()).queue();
            });//.getUrl();
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
