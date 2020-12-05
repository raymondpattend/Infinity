package com.reflexian.discordbot.listeners;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.commands.fun.Say;
import com.reflexian.discordbot.commands.moderation.Ban;
import com.reflexian.discordbot.commands.moderation.Unban;
import com.reflexian.discordbot.commands.utilities.Config;
import com.reflexian.discordbot.commands.utilities.Help;
import com.reflexian.discordbot.commands.utilities.Info;
import com.reflexian.discordbot.commands.utilities.Uptime;
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
                    Config config = new Config(args, event.getMember(), event.getAuthor());
                    try {
                        config.execute(event);
                    } catch (SQLException throwables) {
                        Main.logger.error(throwables.getLocalizedMessage());
                    }
                    config = null;
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
                default:
                    event.getChannel().sendMessage("I do not understand that command, " + event.getAuthor().getAsMention() + " \\:(").queue(message1 -> {
                        message1.delete().queueAfter(10, TimeUnit.SECONDS);
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
