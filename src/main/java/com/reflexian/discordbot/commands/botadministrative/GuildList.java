package com.reflexian.discordbot.commands.botadministrative;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.listeners.CommandListener;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;

public class GuildList extends Command {
    public GuildList(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    private long id;
    private int page;
    private Member member;

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException, InsufficientPermissionException {
        TextChannel textChannel = event.getTextChannel();
        if (textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE)&&textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.VIEW_CHANNEL)) {
            textChannel.sendMessage(getPage(1).build()).queue(message -> {
                this.page = 1;
                this.id = message.getIdLong();
                message.addReaction(":leftarrow:786734116179673099").queue();
                message.addReaction(":redCross:761713140957184000").queue();
                message.addReaction(":rightarrow:786734065079943229").queue();
                this.member = event.getMember();
            });
        }
        CommandListener.registerEvent(this);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) throws IllegalStateException {
        if (Objects.requireNonNull(event.getMember()).getUser().isBot()) return;
        if (event.getChannel().getType().isGuild()&&event.getMessageIdLong()==this.id&&event.getMember()==this.member) {
            JDA jda = Main.getJda();
            int friendsPerPage = 10;
            int friendsIHave = jda.getGuilds().size();
            int pages = (friendsIHave / friendsPerPage ) + (friendsIHave % friendsPerPage  == 0 ? 0 : 1);

            // EMOTE is a discord entity whist EMOJI is a normal EMOJI

            if (event.getReactionEmote().isEmoji()) {
                event.retrieveMessage().queue(message -> {
                    message.removeReaction(event.getReactionEmote().getEmoji(), event.getMember().getUser()).queue();
                });
                return;
            }
            event.retrieveMessage().queue(message -> {
                message.removeReaction(event.getReactionEmote().getEmote(), event.getMember().getUser()).queue();
            });

            switch (event.getReactionEmote().getId()) {
                case "786734116179673099":
                    event.retrieveMessage().queue(message -> {
                        if (page==1) {
                            return;
                        }
                        message.editMessage(getPage(page-1).build()).queue();
                        page--;
                    });
                    break;
                case "786734065079943229":
                    event.retrieveMessage().queue(message -> {
                        if (page==pages) {
                            return;
                        }
                        message.editMessage(getPage(page+1).build()).queue();
                        page++;
                    });
                    break;
                case "761713140957184000":
                    event.retrieveMessage().queue(message -> message.delete().queue());
                    break;
            }
        } else if (event.getMember() != this.member&&event.getMessageIdLong()==this.id) {
            event.retrieveMessage().queue(message -> {
                if (event.getReaction().getReactionEmote().isEmoji()) message.removeReaction(event.getReactionEmote().getEmoji(), event.getMember().getUser()).queue();
                else message.removeReaction(event.getReactionEmote().getEmote(), event.getMember().getUser()).queue();
            });
        }
    }

    private EmbedBuilder getPage(int pageNumber) {
        JDA jda = Main.getJda();
        int friendsPerPage = 10;
        int friendsIHave = jda.getGuilds().size();

        int max = pageNumber * friendsPerPage;
        int min = max - friendsPerPage;
        int pages = (friendsIHave / friendsPerPage ) + (friendsIHave % friendsPerPage  == 0 ? 0 : 1);

        EmbedBuilder em = new EmbedBuilder();
        em.setColor(new Color(72, 173, 241));
        em.setTitle("Guild List (" +pageNumber+"/"+pages+")" );
        em.setDescription("This is the list of guilds currently using the bot.");
        try {
            for(int i = min + 1; i <= max; i++) {
                Guild guild = jda.getGuilds().get(i-1);
                int members = 0;
                for (Member member : guild.getMembers()) {
                    if (member.getUser().isBot()) continue;
                    members++;
                }
                if (MySQL.getBool("guild_data", "verified", "guild_id", guild.getId())) em.addField("#"+i+" "+"<:verified:785194601240723507> ["+guild.getName()+"]", guild.getOwner().getUser().getAsTag() + " | " + guild.getMemberCount()  + "/" + (guild.getMemberCount()-members) + " | " + guild.getId(), false);
                else em.addField("#"+i+" " +guild.getName()+"", guild.getOwner().getUser().getAsTag() + " | " + members + "/" + (guild.getMemberCount()-members) + " | " + guild.getId(), false);
            }
        }catch (IndexOutOfBoundsException ignored) {}
        em.setFooter("Only executable by bot administrators.");
        return em;
    }

    @Override
    public void cancel() {

    }
}
