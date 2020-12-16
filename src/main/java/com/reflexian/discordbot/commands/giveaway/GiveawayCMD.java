package com.reflexian.discordbot.commands.giveaway;

import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.listeners.CommandListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class GiveawayCMD extends Command {
    public GiveawayCMD(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        switch (args[2].toLowerCase()) {
            case "start":
                CommandListener.registerEvent(this);
                try{
                    int mins = Integer.parseInt(args[3]);
                    EmbedBuilder giveaway = new EmbedBuilder().setTitle(args[4]).addField("**Prize**","• " + args[4]+"\n\n\n ", false).addField("**Requirement:**", "• You must be above level 5 to enter\n\nReact \uD83C\uDF89 to enter!\n*You will not be able to win if you are not level 5 or higher*", false).setFooter("Winners will be selected in " + mins + " minutes!").setThumbnail(event.getGuild().getIconUrl());
                    event.getChannel().sendMessage(giveaway.build()).queue(message -> {
                        message.addReaction("\uD83C\uDF89").queue();
                        new Giveaway(mins,message,args[4]).start();
                    });
                } catch(NumberFormatException ex)
                {
                    event.getChannel().sendMessage("Could not parse minutes from `"+args[3]+"`").queue();
                }
                return;
            case "reroll":
                if(!args[3].matches("\\d{17,22}"))
                {
                    event.getChannel().sendMessage("Invalid message id").queue();
                    return;
                }
                event.getChannel().retrieveMessageById(args[3]).queue(message -> {
                    if(message==null)
                    {
                        event.getChannel().sendMessage("Message not found!").queue();
                        return;
                    }
                    message.getReactions()
                            .stream().filter(mr -> mr.getReactionEmote().getName().equals("\uD83C\uDF89"))
                            .findAny().ifPresent(mr -> {
                        List<User> users = new LinkedList<>();
                        for (User ursO : mr.retrieveUsers()) {
                            users.add(ursO);
                        }
                        users.remove(message.getJDA().getSelfUser());
                        String id = users.get((int) (Math.random() * users.size())).getId();
                        EmbedBuilder giveaway = new EmbedBuilder().setTitle("Giveaway Reroll!").addField("**Prize**","• ???" +"\n\n\n", false).addField("**WINNER:**", "<@"+id+">"+"\n\n\n\n*The giveaway has ended!*", false).setFooter("Winners have been selected.").setThumbnail(message.getGuild().getIconUrl());
                        message.editMessage(giveaway.build()).queue();
                        message.getChannel().sendMessage("Congratulations to <@" + id + ">! You won the reroll!").queue();
                    });
                });
        }
    }


/*    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("!ghelp"))
        {
            event.getChannel().sendMessage("<:yay:294906617378504704> GiveawayBot help: <:yay:294906617378504704>\n"
                    + "`!ghelp` - this message\n"
                    + "`!gstart <seconds> [item]` - starts a giveway. Ex: `!gstart 180` for a 3 minute giveaway\n"
                    + "`!greroll <messageid>` - rerolls a winner for the giveaway on the provided message\n\n"
                    + "Commands require Manage Server permission to use\n"
                    + "Don't include <> nor []; <> means required, [] means optional").queue();
        }
        else if(event.getMessage().getContentRaw().startsWith("!gstart"))
        {
            if(!event.getMember().hasPermission(Permission.MANAGE_SERVER))
            {
                event.getChannel().sendMessage("You must have Manage Server perms to use this!").queue();
                return;
            }
            String str = event.getMessage().getContentRaw().substring(7).trim();
            String[] parts = str.split("\\s+",2);
            try{
                int sec = Integer.parseInt(parts[0]);
                event.getChannel().sendMessage("<:yay:294906617378504704>  **GIVEAWAY!**  <:yay:294906617378504704>\n"+(parts.length>1 ? "\u25AB*`"+parts[1]+"`*\u25AB\n" : "")+"React with \uD83C\uDF89 to enter!").queue(m -> {
                    m.addReaction("\uD83C\uDF89").queue();
                    new Giveaway(sec,m,parts.length>1 ? parts[1] : null).start();
                });
                event.getMessage().delete().queue();
            } catch(NumberFormatException ex)
            {
                event.getChannel().sendMessage("Could not parse seconds from `"+parts[0]+"`").queue();
            }
        }
        else if(event.getMessage().getContentRaw().startsWith("!greroll"))
        {
            if(!event.getMember().hasPermission(Permission.MANAGE_SERVER))
            {
                event.getChannel().sendMessage("You must have Manage Server perms to use this!").queue();
                return;
            }
            String id = event.getMessage().getContentRaw().substring(8).trim();
            if(!id.matches("\\d{17,22}"))
            {
                event.getChannel().sendMessage("Invalid message id").queue();
                return;
            }
            Message m = event.getChannel().getHistory().getMessageById(id);
            if(m==null)
            {
                event.getChannel().sendMessage("Message not found!").queue();
                return;
            }
            m.getReactions()
                    .stream().filter(mr -> mr.getReactionEmote().getName().equals("\uD83C\uDF89"))
                    .findAny().ifPresent(mr -> {
                List<User> users = new LinkedList<>(mr.retrieveUsers().complete());
                users.remove(m.getJDA().getSelfUser());
                String uid = users.get((int)(Math.random()*users.size())).getId();
                event.getChannel().sendMessage("Congratulations to <@"+uid+">! You won the reroll!").queue();
            });
        }
        else if(event.getAuthor().getId().equals("OWNERID"))
        {
            if(event.getMessage().getContentRaw().startsWith("!say"))
                event.getChannel().sendMessage(event.getMessage().getContentRaw().substring(4).trim()).queue();
            else if(event.getMessage().getContentRaw().startsWith("!ava"))
            {
                String loc = event.getMessage().getContentRaw().substring(4).trim();
                try{
                    event.getJDA().getSelfUser().getManager().setAvatar(Icon.from(new File(loc))).complete();
                    event.getChannel().sendMessage("Updated!").queue();
                }catch(Exception ex){
                    event.getChannel().sendMessage("Error: "+ex).queue();
                }
            }
        }
    }*/

    @Override
    public void cancel() {

    }


    public class Giveaway {

        int seconds;
        Message message;
        String item;
        public Giveaway(int time, Message message, String item)
        {
            seconds = time;
            this.message = message;
            this.item = item;
        }

        private LinkedList<User> reactionUsers = new LinkedList<>();
        private void chooseWinner() {

            //Collections.shuffle(playerList);

            message.getChannel().retrieveMessageById(message.getId()).complete().getReactions()
                    .stream().filter(mr -> mr.getReactionEmote().getName().equals("\uD83C\uDF89"))
                    .findAny().ifPresent(mr -> {
                List<User> users = new LinkedList<>();
                for (User ursO : mr.retrieveUsers()) {
                    users.add(ursO);
                }
                users.remove(message.getJDA().getSelfUser());
                String id = users.get((int) (Math.random() * users.size())).getId();
                EmbedBuilder giveaway = new EmbedBuilder().setTitle(item).addField("**Prize**","• " + item+"\n\n\n", false).addField("**WINNER:**", "<@"+id+">"+"\n\n\n\n*The giveaway has ended!*", false).setFooter("Winners have been selected.").setThumbnail(message.getGuild().getIconUrl());
                message.getEmbeds().get(0);
                message.editMessage(giveaway.build()).queue();
                message.getChannel().sendMessage("Congratulations to <@" + id + ">! You won" + (item == null ? "" : " the " + item) + "!").queue();
            });
        }

        public void start()
        {
            new Thread(){
                @Override
                public void run() {
                    while(seconds>5)
                    {
                        EmbedBuilder giveaway = new EmbedBuilder().setTitle("Giveaway!").addField("**Prize**","• " + item+"\n\n\n ", false).addField("**Requirement:**", "• You must be above level 5 to enter\n\nReact \uD83C\uDF89 to enter!\n*You will not be able to win if you are not level 5 or higher*", false).setFooter("Winners will be selected in " + secondsToTime(seconds) + "!").setThumbnail(message.getGuild().getIconUrl());
                        message.editMessage(giveaway.build()).queue();
                        seconds-=5;
                        try{Thread.sleep(5000);}catch(Exception e){}
                    }
                    while(seconds>0)
                    {
                        EmbedBuilder giveaway = new EmbedBuilder().setTitle("**ENDING SOON - LAST CHANCE**").addField("**Prize**","• " + item+"\n\n\n ", false).addField("**Requirement:**", "• You must be above level 5 to enter\n\nReact \uD83C\uDF89 to enter!\n*You will not be able to win if you are not level 5 or higher*", false).setFooter("Winners will be selected in " + secondsToTime(seconds) + "!").setThumbnail(message.getGuild().getIconUrl());
                        message.editMessage(giveaway.build()).queue();
                        seconds--;
                        try{Thread.sleep(1000);}catch(Exception e){}
                    }
                    message.getChannel().retrieveMessageById(message.getIdLong()).queue(message1 -> System.out.println(message1.getReactions().size()));
                    chooseWinner();
                }
            }.start();
        }
    }

    public static String secondsToTime(long timeseconds)
    {
        StringBuilder builder = new StringBuilder();
        int years = (int)(timeseconds / (60*60*24*365));
        if(years>0)
        {
            builder.append("**").append(years).append("** years, ");
            timeseconds = timeseconds % (60*60*24*365);
        }
        int weeks = (int)(timeseconds / (60*60*24*365));
        if(weeks>0)
        {
            builder.append("**").append(weeks).append("** weeks, ");
            timeseconds = timeseconds % (60*60*24*7);
        }
        int days = (int)(timeseconds / (60*60*24));
        if(days>0)
        {
            builder.append("**").append(days).append("** days, ");
            timeseconds = timeseconds % (60*60*24);
        }
        int hours = (int)(timeseconds / (60*60));
        if(hours>0)
        {
            builder.append("**").append(hours).append("** hours, ");
            timeseconds = timeseconds % (60*60);
        }
        int minutes = (int)(timeseconds / (60));
        if(minutes>0)
        {
            builder.append("**").append(minutes).append("** minutes, ");
            timeseconds = timeseconds % (60);
        }
        if(timeseconds>0)
            builder.append("**").append(timeseconds).append("** seconds");
        String str = builder.toString();
        if(str.endsWith(", "))
            str = str.substring(0,str.length()-2);
        if(str.equals(""))
            str="**No time**";
        return str;
    }
}