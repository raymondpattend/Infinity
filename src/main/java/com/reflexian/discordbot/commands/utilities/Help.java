package com.reflexian.discordbot.commands.utilities;

import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Help extends Command {


    public Help(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        EmbedBuilder help = new EmbedBuilder();
        help.setColor(new Color(67, 149, 19));
        help.setTitle("Help");

        help.setDescription("These are the commands available to you! :)");

        help.addField("Fun/Other", "Say **-** Make me say something\nMusic **-** Play some music!\nPrivacy **-** Learn about what data we store and how we use it.", false);

        if (MySQL.getBool("guild_data","level_enabled", "guild_id", event.getGuild().getId())) help.addField("Utilities", "Help **-** Get commands and usages\nInfo **-** Information about Infinity\nUptime **-** Get an uptime report\nUserInfo **-** Get information about a user\nLeaderboard **-** Get the level leaderboard\nLevel **-** Get the level of a user", false);
        else help.addField("Utilities", "Help **-** Get commands and usages\nInfo **-** Information about Infinity\nUptime **-** Get an uptime report\nUserInfo **-** Get information about a user", false);

        if (MySQL.hasMembership(event.getGuild())) {
            help.addField("Membership","Membership **-** Get information about this guild's membership", false);
        }

        if (event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            help.addField("Moderation", "Ban **-** Permanently Ban a user\nUnban **-** Unban a banned user\nBanList **-** List all the banned players",false);
        }
        if (event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            help.addField("Administrator","Config **-** Configure settings\nLeveling **-** Change leveling settings\nLog **-** Log messages to a channel",false); // Leveling **-** Change leveling settings
        }
        if (event.getAuthor().getIdLong() == 269262067503071232L) {
            help.addField("Bot Administrator", "AddMembership **-** Add membership to guild\nGetInvite **-** Get an invite to a specific guild\nVerify **-** Verify a guild (not a bot)\nGuildList **-** Get the list of guilds the bot is in\nLeave **-** Make the bot leave a certain guild", false);
        }
        help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
        sendMessage(event.getTextChannel(), help.build(), null);
    }

    @Override
    public void cancel() {

    }
}
