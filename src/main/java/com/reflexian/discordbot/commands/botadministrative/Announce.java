package com.reflexian.discordbot.commands.botadministrative;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.listeners.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;

public class Announce extends Command {
    public Announce(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    private EmbedBuilder announcement = new EmbedBuilder().setColor(new Color(54, 85, 177));
    private long id;
    private Member member;
    private String message;


    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args.length <3) {

        }

        StringBuilder message = new StringBuilder();
        for (String string : args) {
            if (string.equals(args[0])||string.equals(args[1])) continue;
            message.append(string).append(" ");
        }
        this.message=message.toString();

        TextChannel textChannel = event.getTextChannel();
        if (textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE)&&textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.VIEW_CHANNEL)) {
            textChannel.sendMessage(announcement.build()).queue(message1 -> {
                this.id = message1.getIdLong();
                message1.addReaction(":greenCheck:761713176882053190").queue();
                message1.addReaction(":redCross:761713140957184000").queue();
                this.member = event.getMember();
            });
        }
        CommandListener.registerEvent(this);

    }


    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getMember().getUser().isBot()) return;
        if (event.getChannel().getType().isGuild()&&event.getMessageIdLong()==this.id&&event.getMember()==this.member) {
            JDA jda = Main.getJda();

            // EMOTE is a discord entity whist EMOJI is a normal EMOJI

            if (event.getReactionEmote().isEmoji()) {
                event.retrieveMessage().queue(message -> {
                    message.removeReaction(event.getReactionEmote().getEmoji(), event.getMember().getUser()).queue();
                });
                return;
            }
            event.retrieveMessage().queue(message -> {
                message.getEmotes().clear();
            });

            switch (event.getReactionEmote().getId()) {
                // CHECKMARK
                case "761713176882053190":
                    event.retrieveMessage().queue(message -> {

                    });
                    for (Guild guild : Main.getJda().getGuilds()) {
                        guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> {

                            announcement.setTitle("Important Announcement - Infinity");
                            announcement.setDescription("This is an important announcement from the developers of Infinity. Don't worry, only the server owners are sent this message (we respect your communities).");
                            announcement.addField("**Information**", message.toString(), false);

                            privateChannel.sendMessage(announcement.build()).queue();
                        });
                    }
                    break;
                case "761713140957184000":
                    event.retrieveMessage().queue(message -> {
                        message.delete().queue();
                    });
                    break;
            }
        } else if (event.getMember() != this.member&&event.getMessageIdLong()==this.id) {
            event.retrieveMessage().queue(message -> {
                if (event.getReaction().getReactionEmote().isEmoji()) message.removeReaction(event.getReactionEmote().getEmoji(), event.getMember().getUser()).queue();
                else message.removeReaction(event.getReactionEmote().getEmote(), event.getMember().getUser()).queue();
            });
        }
    }

    @Override
    public void cancel() {

    }
}
