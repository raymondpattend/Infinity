package com.reflexian.discordbot.commands.administrative;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Config extends Command {

    public Config(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }
    private final EmbedBuilder help = new EmbedBuilder().setTitle("Help - Config").setDescription("Sub Commands start with <@775250061504413727>").addField("**Note**", "We have a webpanel in development to help make our bot easier to configure.", false).addField("Commands","join_message_enabled **-** Enable/Disabe the join message\njoin_message **-** Set the join message\njoin_role_enabled **-** Enable/Disable the join role\njoin_role_id **-** Set the join role's ID (must be valid)\nget_role **-** Get information about autoroles in this guild\nget_message **-** Get information about the join message in this guild",false).addField("Description", "The config command allows you to customize Infinity for your guild.", false).addField("Permission", "Requires ``MANAGE_SERVER`` to execute subcommands.", false).setColor(new Color(189,55,55));

    @Override
    public void execute(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        // @Infinity join_message/join_message_enabled/join_role_enabled/join_role_id <value>

        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
            sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No permission.").setDescription("You need ``MANAGE_SERVER`` permission to use this command!").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
            return;
        }

        if (args.length <3) {
            sendMessage(event.getTextChannel(), help.build(), 40);
            return;
        }

        switch (args[2].toLowerCase()) {
            case "get_message":
                EmbedBuilder a = new EmbedBuilder().setColor(new Color(163, 46, 46)).setTitle("Join Message Information").setDescription("This is information about the join message settings for this guild.\n**Enabled** - " + MySQL.getBool("guild_data", "join_message_enabled", "guild_id", event.getGuild().getId())+"\n**Current Message** - ```" + MySQL.getString("guild_data", "join_message", "guild_id", event.getGuild().getId()) +"```\n").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                sendMessage(event.getTextChannel(), a.build(), 25);
                break;
            case "get_role":
                try {
                    EmbedBuilder b = new EmbedBuilder().setColor(new Color(163, 46, 46)).setTitle("Join Role Information").setDescription("This is information about the join role settings for this guild.\n**Enabled** - " + MySQL.getBool("guild_data", "join_role_enabled", "guild_id", event.getGuild().getId())+"\n**Current Role** - ```" + Objects.requireNonNull(event.getGuild().getRoleById(Objects.requireNonNull(MySQL.getString("guild_data", "join_role_id", "guild_id", event.getGuild().getId())))).getName() + "```\n").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), b.build(), 25);
                }catch (NullPointerException e) {
                    EmbedBuilder b = new EmbedBuilder().setColor(new Color(163, 46, 46)).setTitle("Join Role Information").setDescription("This is information about the join role settings for this guild.\n**Enabled** - " + MySQL.getBool("guild_data", "join_role_enabled", "guild_id", event.getGuild().getId())+"\n**Current Role** - N/A\n").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), b.build(), 25);
                }
                break;
            case "join_message":
                StringBuilder str = new StringBuilder();
                try {
                    for (String string : args) {
                        if (string.equals(args[0])||string.equals(args[1])||string.equals(args[2])) {
                            continue;
                        }
                        str.append(string).append(" ");
                    }

                    MySQL.setString("guild_data", "join_message", str.toString(), "guild_id", event.getGuild().getId());

                    EmbedBuilder em = new EmbedBuilder().setTitle("Successfully updated welcome message.").setDescription("You have successfully updated the welcoming message for " + event.getGuild().getName() + ".").addField("New Message", "```"+str+"```", false).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), em.build(), 25);
                    return;

                }catch (IndexOutOfBoundsException e) {
                    sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("Not enough arguments.").setDescription("You must include a value!\n**Example**: ``@Infinity#9833 config <join_message/join_message_enabled/join_role_enabled/join_role_id> <value>``\n**Hint**: ``%guild_name%`` is replaced with your guild name!").setColor(new Color(168, 42, 42)).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).build(), 10);
                    return;
                }
            case "join_message_enabled":
                EmbedBuilder value = new EmbedBuilder();
                if (args[3].equalsIgnoreCase("true")) {
                    value.setColor(new Color(43, 167, 60));
                    value.setTitle("Successfully updated value: join_message_enabled");
                    value.setDescription("The value \"join_message_enabled\" has been updated to true.");
                    value.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    MySQL.setBool("guild_data", "join_message_enabled", true, "guild_id", event.getGuild().getId());
                } else if (args[3].equalsIgnoreCase("false")) {
                    value.setColor(new Color(43, 167, 60));
                    value.setTitle("Successfully updated value: join_message_enabled");
                    value.setDescription("The value \"join_message_enabled\" has been updated to false.");
                    value.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    MySQL.setBool("guild_data", "join_message_enabled", false, "guild_id", event.getGuild().getId());
                } else {
                    value.setColor(new Color(208, 51, 51));
                    value.setTitle("Not a valid argument.");
                    value.setDescription("You can only use \"true\" or \"false\".");
                    value.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), value.build(), 10);
                    return;
                }
                sendMessage(event.getTextChannel(), value.build(), 25);
                return;
            case "join_role_enabled":
                EmbedBuilder value1 = new EmbedBuilder();
                value1.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                if (args[3].equalsIgnoreCase("true")) {
                    value1.setColor(new Color(43, 167, 60));
                    value1.setTitle("Successfully updated value: join_role_enabled");
                    value1.setDescription("The value \"join_role_enabled\" has been updated to true.");
                    MySQL.setBool("guild_data", "join_role_enabled", true, "guild_id", event.getGuild().getId());
                } else if (args[3].equalsIgnoreCase("false")) {
                    value1.setColor(new Color(43, 167, 60));
                    value1.setTitle("Successfully updated value: join_role_enabled");
                    value1.setDescription("The value \"join_role_enabled\" has been updated to false.");
                    MySQL.setBool("guild_data", "join_role_enabled", false, "guild_id", event.getGuild().getId());
                } else {
                    value1.setColor(new Color(208, 51, 51));
                    value1.setTitle("Not a valid argument.");
                    value1.setDescription("You can only use \"true\" or \"false\".");
                    sendMessage(event.getTextChannel(), value1.build(), 10);
                    return;
                }
                sendMessage(event.getTextChannel(), value1.build(), 25);
                return;
            case "join_role_id":
                try {

                    long id = Long.parseLong(args[3]);
                    if (event.getGuild().getRoleById(id)==null) {
                        EmbedBuilder em1 = new EmbedBuilder();
                        em1.setColor(new Color(208, 51, 51));
                        em1.setTitle("Not a valid argument.");
                        em1.setDescription("You can only use role ids.");
                        sendMessage(event.getTextChannel(), em1.build(), 10);
                        return;
                    }
                    MySQL.setString("guild_data", "join_role_id", String.valueOf(id), "guild_id", event.getGuild().getId());

                    EmbedBuilder em = new EmbedBuilder().setTitle("Successfully updated value : join_role_id").setDescription("The value \"join_role_id\" has been set to " + id+".").setColor(new Color(43, 167, 60)).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(),em.build(),25);
                }catch (NumberFormatException | NullPointerException e) {
                    EmbedBuilder em1 = new EmbedBuilder();
                    em1.setColor(new Color(208, 51, 51));
                    em1.setTitle("Not a valid argument.");
                    em1.setDescription("You can only use role ids.\n**WARNING** This will only work if the bot has a higher role than the role you are assigning at join!!!");
                    sendMessage(event.getTextChannel(), em1.build(), 10);
                }
        }

    }

    @Override
    public void cancel() {

    }
}
