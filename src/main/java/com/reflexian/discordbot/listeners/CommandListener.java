package com.reflexian.discordbot.listeners;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.commands.administrative.Config;
import com.reflexian.discordbot.commands.administrative.Log;
import com.reflexian.discordbot.commands.botadministrative.*;
import com.reflexian.discordbot.commands.fun.Say;
import com.reflexian.discordbot.commands.leveling.Leaderboard;
import com.reflexian.discordbot.commands.administrative.Leveling;
import com.reflexian.discordbot.commands.leveling.Rank;
import com.reflexian.discordbot.commands.membership.Membership;
import com.reflexian.discordbot.commands.moderation.Ban;
import com.reflexian.discordbot.commands.moderation.BanList;
import com.reflexian.discordbot.commands.moderation.Unban;
import com.reflexian.discordbot.commands.music.Music;
import com.reflexian.discordbot.commands.utilities.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getChannelType().isGuild()) return;
        String message = event.getMessage().getContentRaw();
        String[] args = message.split("\\s+");
        if (message.startsWith("<@775250061504413727> ") || message.startsWith("<@!775250061504413727> ") || message.startsWith("<@!784515176132378625> ") || message.startsWith("<@784515176132378625> ")) {
            if (message.startsWith("<@!784515176132378625> ") || message.startsWith("<@784515176132378625> ")) {
                if (!Main.isDev) return;
            } else if (message.startsWith("<@775250061504413727> ") || message.startsWith("<@!775250061504413727> ")) {
                if (Main.isDev) return;
            }
            message = message.replace("<@775250061504413727> ", "").replace("<@!775250061504413727> ", "").replace("<@!784515176132378625> ", "").replace("<@784515176132378625> ", "");
            try {
                commandExecutor(event, message);
            } catch (SQLException | ExecutionException | InterruptedException throwables) {
                throwables.printStackTrace();
            }
        } else if (message.toLowerCase().startsWith("i! ") || message.toLowerCase().startsWith("!i")) {
            //if (Main.isDev) return;
            message=message.toLowerCase().replace("i! ", "").replace("!i ", "");
            try {
                commandExecutor(event, message);
            } catch (SQLException | ExecutionException | InterruptedException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    private void commandExecutor(MessageReceivedEvent event, String message) throws SQLException, ExecutionException, InterruptedException {
        if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) event.getMessage().delete().queue();
        String[] args = message.split("\\s+");
        Main.logger.info(event.getAuthor().getAsTag() + " -> " + message + " (" + event.getGuild().getName() + ")");
        switch (args[0].toLowerCase()) {
            case "uptime":
                Uptime uptime = new Uptime(args, event.getMember(), event.getAuthor());
                uptime.execute(event);
                uptime = null;
                break;
            case "ban":
                Ban ban = new Ban(args, event.getMember(), event.getAuthor());
                ban.execute(event);
                ban = null;
                break;
            case "unban":
                Unban unban = new Unban(args, event.getMember(), event.getAuthor());
                unban.execute(event);
                unban = null;
                break;
            /*case "bug":
            case "bugreport":
                Bug bug = new Bug(args, event.getMember(), event.getAuthor());
                bug.execute(event);
                bug=null;
                break;*/
            case "log":
                Log log = new Log(args, event.getMember(), event.getAuthor());
                log.execute(event);
                log=null;
                return;
            case "say":
                Say say = new Say(args, event.getMember(), event.getAuthor());
                say.execute(event);
                say = null;
                break;
            case "announce":
                if (event.getAuthor().getIdLong() == 269262067503071232L) {
                    Announce announce = new Announce(args, event.getMember(), event.getAuthor());
                    announce.execute(event);
                    announce=null;
                }
                break;
            case "config":
                Config config = new Config(args, event.getMember(), event.getAuthor());
                config.execute(event);
                config = null;
                break;
            case "getinvite":
                if (event.getAuthor().getIdLong() == 269262067503071232L) {
                    GetInvite getInvite = new GetInvite(args, event.getMember(), event.getAuthor());
                    getInvite.execute(event);
                    getInvite = null;
                }
                break;
            case "leaderboard":
                Leaderboard leaderboard = new Leaderboard(args, event.getMember(), event.getAuthor());
                leaderboard.execute(event);
                leaderboard = null;
                break;
            /*case "giveaway":
                GiveawayCMD giveaway = new GiveawayCMD(args, event.getMember(), event.getAuthor());
                giveaway.execute(event);
                giveaway=null;
                return;*/
            case "privacy":
                Privacy privacy = new Privacy(args, event.getMember(), event.getAuthor());
                privacy.execute(event);
                privacy=null;
                break;
            case "level":
            case "rank":
                Rank rank = new Rank(args, event.getMember(), event.getAuthor());
                rank.execute(event);
                rank = null;
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
                    guildList.execute(event);
                    guildList = null;
                    break;
                }
                break;
            case "leveling":
                Leveling leveling = new Leveling(args, event.getMember(), event.getAuthor());
                leveling.execute(event);
                leveling=null;
                break;
            case "addmembership":
                if (event.getAuthor().getIdLong() == 269262067503071232L) {
                    AddMembership addMembership = new AddMembership(args, event.getMember(), event.getAuthor());
                    addMembership.execute(event);
                    addMembership = null;
                }
                break;
            case "membership":
                Membership membership = new Membership(args, event.getMember(), event.getAuthor());
                membership.execute(event);
                membership=null;
                break;
            /*case "verificationtest":
                Captcha captcha = new Captcha(150, 50);
                captcha.background();
                captcha.text(5);
                captcha.noiseStraightLine();
                captcha.distortionShear();
                captcha.noiseStrokes();
                BufferedImage image = captcha.getImage();

                File file = new File("temp.png");
                try {
                    ImageIO.write(image, "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.getChannel().sendFile(file).queue();
                event.getChannel().sendMessage(captcha.getText()).queue();
                break;*/
            case "verify":
                if (event.getAuthor().getIdLong() == 269262067503071232L) {
                    Verify verify = new Verify(args, event.getMember(), event.getAuthor());
                    verify.execute(event);
                    verify = null;
                }
                break;
            case "music":
                Music music = new Music(args, event.getMember(), event.getAuthor());
                registerEvent(music);
                music.execute(event);
                music = null;
                break;
            case "user":
            case "userinfo":
                User user = new User(args, event.getMember(), event.getAuthor());
                user.execute(event);
                user = null;
                break;
            case "leave":
                if (event.getAuthor().getIdLong() == 269262067503071232L) {
                    Leave leave = new Leave(args, event.getMember(), event.getAuthor());
                    leave.execute(event);
                    leave = null;
                    break;
                }
                break;
            case "banlist":
                BanList banList = new BanList(args, event.getMember(), event.getAuthor());
                banList.execute(event);
                banList = null;
                break;
            /*case "warn":
            case "warning":
            case "warnings":
                Warn warn = new Warn(args, event.getMember(), event.getAuthor());
                try {
                    warn.execute(event);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                warn = null;
                break;*/
            default:
                event.getChannel().sendMessage("I do not understand that command, " + event.getAuthor().getAsMention() + " \\:(").queue(message1 -> {
                    message1.delete().queueAfter(10, TimeUnit.SECONDS);
                }, (failure) -> {
                });
                break;
        }
    }

    public static void registerEvent(Object obj) {
        Main.getJda().addEventListener(obj);
    }

    public static void unregisterEvent(Object obj) {
        Main.getJda().removeEventListener(obj);
    }
}
