package com.reflexian.discordbot.events.log;

import com.reflexian.discordbot.commands.fun.Embed;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MessageLoader extends ListenerAdapter {

    public Map<Long, Message> messageMap = new HashMap<>();
    public int i = 0;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = event.getMessage().getContentRaw();
        if (message.startsWith("<@775250061504413727> ") || message.startsWith("<@!775250061504413727> ") || message.startsWith("<@!784515176132378625> ") || message.startsWith("<@784515176132378625> ")||message.toLowerCase().startsWith("i! ") || message.toLowerCase().startsWith("!i")) return;
        messageMap.put(event.getMessageIdLong(), event.getMessage());
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event){
        if (isEnabled(event.getGuild().getIdLong())&&messageMap.containsKey(event.getMessageIdLong())){
            if (messageMap.get(event.getMessageIdLong()).getContentRaw().equals("")) return;
            TextChannel textChannel = getChannel(event.getGuild());
            if (textChannel==null) return;
            Message message = messageMap.get(event.getMessageIdLong());
            EmbedBuilder edited = new EmbedBuilder().setTitle("\ud83d\uddd1\ufe0f Deleted Message").setColor(new Color(76, 119, 167)).setDescription("Message sent by " + message.getAuthor().getAsMention() + " was deleted in " +message.getTextChannel().getAsMention()+".").addField("Message", "```"+message.getContentRaw()+"```", false).setTimestamp(new Date().toInstant()).setFooter("Message ID (" + message.getId()+")");
            sendMessage(textChannel, edited.build(), null);
            messageMap.remove(event.getMessageIdLong());
        }
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        if (event.getAuthor().isBot()) return;
        if (isEnabled(event.getGuild().getIdLong())&&messageMap.containsKey(event.getMessageIdLong())){
            TextChannel textChannel = getChannel(event.getGuild());
            if (textChannel==null) return;
            EmbedBuilder edited = new EmbedBuilder().setTitle("\u270f\ufe0f Edited Message").setColor(new Color(76, 119, 167)).setDescription(event.getAuthor().getAsMention() + " edited a message in " + event.getChannel().getAsMention()+". [Click here to go.]("+event.getMessage().getJumpUrl()+")").addField("Before", "```"+messageMap.get(event.getMessageIdLong()).getContentRaw()+"```", false).addField("After", "```"+event.getMessage().getContentRaw()+"```", false).setTimestamp(new Date().toInstant()).setFooter("Edited by " + event.getAuthor().getAsTag());
            sendMessage(textChannel, edited.build(), null);
            messageMap.replace(event.getMessageIdLong(), event.getMessage());
        }
    }

    private boolean isEnabled(long id) {
        return MySQL.getBool("guild_data", "logging_enabled", "guild_id", id + "");
    }

    private TextChannel getChannel(Guild guild) {
        long id = Long.parseLong(MySQL.getString("guild_data", "logging_channel", "guild_id", guild.getId()));
        if (id==0) return null;
        return guild.getTextChannelById(id);
    }

    public void sendMessage(TextChannel textChannel, MessageEmbed embed, @Nullable Integer secondDelete) {
        if (textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE)&&textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.VIEW_CHANNEL)) {
            textChannel.sendMessage(embed).queue(message -> {
                if (secondDelete==null) return;
                if (message == null) return;
                message.delete().queueAfter(secondDelete, TimeUnit.SECONDS);
            });
        }
    }

    public void sendMessage(TextChannel textChannel, String text, @Nullable Integer secondDelete) {
        if (textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE)&&textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.VIEW_CHANNEL)) {
            textChannel.sendMessage(text).queue(message -> {
                if (secondDelete==null) return;
                if (message==null) return;
                message.delete().queueAfter(secondDelete, TimeUnit.SECONDS);
            });
        }
    }
}
