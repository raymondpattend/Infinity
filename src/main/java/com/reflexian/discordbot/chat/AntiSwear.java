package com.reflexian.discordbot.chat;

import com.reflexian.discordbot.events.log.MessageLoader;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class AntiSwear extends ListenerAdapter {

    private static final Map<String, AntiSwear> filters = new HashMap<>();
    private final String identifier;
    private final Map<CheckType, List<Object>> checkMap = new HashMap<>();

    MessageLoader messageLoader = new MessageLoader();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getGuild().getId().equals("770142850633433094")) return;
        if (event.getMember().hasPermission(Permission.KICK_MEMBERS)) return;
        AntiSwear antiSwear = new AntiSwear("antiswear");
        antiSwear.addCheck(words, CheckType.EXACT);
        antiSwear.addCheck(words, CheckType.REGEX);
        if (antiSwear.checkAll(event.getMessage().getContentRaw()) >= 50) {
            //EmbedBuilder blacklistedWord = new EmbedBuilder().setTitle("\ud83d\udcdc Deleted Blacklisted Message").setColor(new Color(76, 119, 167)).setDescription("Message sent by " + event.getAuthor().getAsMention() + " was deleted in " +event.getMessage().getTextChannel().getAsMention()+".").addField("Message", "```"+event.getMessage().getContentRaw()+"```", false).setTimestamp(new Date().toInstant()).setFooter("Message ID (" + event.getMessage().getId()+")");

            EmbedBuilder mess = new EmbedBuilder();
            mess.setColor(new Color(198, 76, 76));
            mess.setTitle("Blacklisted Word");
            mess.setDescription("A recent word or sentence you posted contained a blacklisted word. Because of this, we removed your message.");
            mess.addField("Your message", event.getMessage().getContentRaw(), false);
            mess.addField("Violation Count", antiSwear.checkAll(event.getMessage().getContentRaw()) + "", false);
            mess.setFooter("Sent to " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            event.getMessage().delete().queueAfter(10, TimeUnit.MILLISECONDS);
            event.getAuthor().openPrivateChannel().queue((channel2) -> {
                channel2.sendMessage(mess.build()).queue();
            });
            /*if (isEnabled(event.getGuild().getIdLong())) {
                TextChannel textChannel = getChannel(event.getGuild());
                if (textChannel==null) return;
                sendMessage(textChannel, blacklistedWord.build(), null);
            }*/

        }
    }

    public enum CheckType {
        EXACT,
        REGEX,
    }

    public AntiSwear(String identifier) {
        this.identifier = identifier;
        this.checkMap.put(CheckType.EXACT, new ArrayList<>());
        this.checkMap.put(CheckType.REGEX, new ArrayList<>());
        filters.put(identifier, this);
    }

    public static AntiSwear getByID(String identifier) {
        return filters.get(identifier);
    }

    public void addCheck(List<String> strings, CheckType... checkTypes) {
        for (CheckType checkType : checkTypes) {
            List<Object> convertedAdd = new ArrayList<>();
            if (checkType == CheckType.REGEX) {
                for (String string : strings) {
                    convertedAdd.add(Pattern.compile(string));
                }
            } this.checkMap.get(checkType).addAll(convertedAdd.isEmpty() ? strings : convertedAdd);
        }
    }

    public int check(CheckType checkType, String string) {
        int violations = 0;
        switch (checkType) {
            case EXACT:
                for (String split : string.split("\\s+")) {
                    for (Object object : checkMap.get(CheckType.EXACT)) {
                        if (object.toString().equalsIgnoreCase(split)) {
                            violations+=100;
                        }
                    }
                }
                break;
            case REGEX:
                for (Object object : checkMap.get(CheckType.REGEX)) {
                    if (((Pattern) object).matcher(string).find()) {
                        violations++;
                    }
                }
                break;
        }
        return violations;
    }

    public int checkAll(String string) {
        return check(CheckType.REGEX, string) + check(CheckType.EXACT, string);
    }



    public static List<String> words = new ArrayList<String>();

    /*public static void saveTheList() {
        URL url = null;
        try {
            url = new URL("https://pastebin.com/raw/VKqjZaCA");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Scanner scanner = null;
        try {
            scanner = new Scanner(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(scanner != null) {
            try {
                words.add(scanner.nextLine());
            }catch (NoSuchElementException e) {
                scanner.close();
                return;
            }
        }
    }*/

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

}
