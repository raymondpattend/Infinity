package com.reflexian.discordbot.listeners;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.commands.administration.GetInvite;
import com.reflexian.discordbot.commands.administration.GuildList;
import com.reflexian.discordbot.commands.administration.Leave;
import com.reflexian.discordbot.commands.fun.Say;
import com.reflexian.discordbot.commands.moderation.Ban;
import com.reflexian.discordbot.commands.moderation.Unban;
import com.reflexian.discordbot.commands.utilities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getChannelType().isGuild()) return;
        String message = event.getMessage().getContentRaw();
        if (message.startsWith("<@775250061504413727> ")||message.startsWith("<@!775250061504413727> ")||message.startsWith("<@!784515176132378625> ")||message.startsWith("<@784515176132378625> ")) {
            if (message.startsWith("<@!784515176132378625> ") || message.startsWith("<@784515176132378625> ")) {
                if (!Main.isDev) return;
            } else if (message.startsWith("<@775250061504413727> ")||message.startsWith("<@!775250061504413727> ")) {
                if (Main.isDev) return;
            }
            event.getMessage().delete().queue();
            message = message.replace("<@775250061504413727> ", "").replace("<@!775250061504413727> ", "").replace("<@!784515176132378625> ", "").replace("<@784515176132378625> ", "");
            String[] args = message.split("\\s+");
            Main.logger.info(event.getAuthor().getAsTag() + " -> " + message + " (" + event.getGuild().getName()+")");
            switch (args[0].toLowerCase()) {
                case "uptime":
                    Uptime uptime = new Uptime(args, event.getMember(), event.getAuthor());
                    uptime.execute(event);
                    uptime=null;
                    break;
                case "ban":
                    Ban ban = new Ban(args, event.getMember(), event.getAuthor());
                    ban.execute(event);
                    ban=null;
                    break;
                case "unban":
                    Unban unban = new Unban(args, event.getMember(), event.getAuthor());
                    unban.execute(event);
                    unban=null;
                    break;
                case "say":
                    Say say = new Say(args, event.getMember(), event.getAuthor());
                    say.execute(event);
                    say=null;
                    break;
                case "config":
                    /*Config config = new Config(args, event.getMember(), event.getAuthor());
                    try {
                        config.execute(event);
                    } catch (SQLException throwables) {
                        Main.logger.error(throwables.getLocalizedMessage());
                    }
                    config = null;*/
                    event.getChannel().sendMessage("This command is disabled, sorry for the inconvenience.").queue(message1 -> message1.delete().queueAfter(10, TimeUnit.SECONDS));
                    break;
                case "getinvite":
                    if (event.getAuthor().getIdLong() == 269262067503071232L) {
                        GetInvite getInvite = new GetInvite(args, event.getMember(), event.getAuthor());
                        try {
                            getInvite.execute(event);
                            getInvite=null;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    break;
                case "info":
                    Info info = new Info(args, event.getMember(), event.getAuthor());
                    info.execute(event);
                    info = null;
                    break;
                case "help":
                    Help help = new Help(args, event.getMember(), event.getAuthor());
                    help.execute(event);
                    help = null;
                    return;
                case "guildlist":
                    if (event.getAuthor().getIdLong() == 269262067503071232L) {
                        GuildList guildList = new GuildList(args, event.getMember(), event.getAuthor());
                        try {
                            guildList.execute(event);
                            guildList=null;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        break;
                    }
                    break;
                /*case "user":
                case "userinfo":
                    User user = new User(args, event.getMember(), event.getAuthor());
                    user.execute(event);
                    user=null;
                    break;*/
                case "leave":
                    if (event.getAuthor().getIdLong() == 269262067503071232L) {
                        Leave leave = new Leave(args, event.getMember(), event.getAuthor());
                        try {
                            leave.execute(event);
                            leave=null;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        break;
                    }
                    break;
                default:
                    event.getChannel().sendMessage("I do not understand that command, " + event.getAuthor().getAsMention() + " \\:(").queue(message1 -> {
                        message1.delete().queueAfter(10, TimeUnit.SECONDS);
                    }, (failure) -> {
                    });
                    break;
            }


        }
    }

    public static void registerEvent(Object obj) {
        Main.getJda().addEventListener(obj);
    }

    public static void unregisterEvent(Object obj) {
        Main.getJda().removeEventListener(obj);
    }

}
