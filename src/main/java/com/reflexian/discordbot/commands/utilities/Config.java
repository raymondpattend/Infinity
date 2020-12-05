package com.reflexian.discordbot.commands.utilities;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class Config extends Command {

    public Config(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        // @Infinity join_message/join_message_enabled/join_role_enabled/join_role_id <value>

        if (args.length <3) {
            event.getChannel().sendMessage(new EmbedBuilder().setTitle("Not enough arguments.").setDescription("You must include a value!\n**Example**: ``@Infinity#9833 config <join_message/join_message_enabled/join_role_enabled/join_role_id> <value>``").setColor(new Color(168, 42, 42)).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
        }

        switch (args[2]) {

            case "join_message":
                StringBuilder str = new StringBuilder();
                try {
                    for (String string : args) {
                        if (string.equals(args[0])||string.equals(args[1])||string.equals(args[2])) {
                            continue;
                        }
                        str.append(string).append(" ");
                    }
                    PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                            .prepareStatement("UPDATE guild_data SET guild_id = ?, join_message = ?");
                    preparedStatement.setLong(1, event.getGuild().getIdLong());
                    preparedStatement.setString(2, str.toString());
                    preparedStatement.execute();
                    EmbedBuilder em = new EmbedBuilder().setTitle("Successfully updated welcome message.").setDescription("You have successfully updated the welcoming message for " + event.getGuild().getName() + ".").addField("New Message", "```"+str+"```", false).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    event.getChannel().sendMessage(em.build()).queue(message -> message.delete().queueAfter(25, TimeUnit.SECONDS));
                    return;

                }catch (IndexOutOfBoundsException e) {
                    event.getChannel().sendMessage(new EmbedBuilder().setTitle("Not enough arguments.").setDescription("You must include a value!\n**Example**: ``@Infinity#9833 config <join_message/join_message_enabled/join_role_enabled/join_role_id> <value>``\n**Hint**: ``%guild_name%`` is replaced with your guild name!").setColor(new Color(168, 42, 42)).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
                    return;
                }
            case "join_message_enabled":
                EmbedBuilder value = new EmbedBuilder();
                PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                        .prepareStatement("UPDATE guild_data SET guild_id = ?, join_message_enabled = ?");
                preparedStatement.setLong(1, event.getGuild().getIdLong());
                if (args[3].equalsIgnoreCase("true")) {
                    value.setColor(new Color(43, 167, 60));
                    value.setTitle("Successfully updated value : join_message_enabled");
                    value.setDescription("The value \"join_message_enabled\" has been updated to true.");
                    value.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    preparedStatement.setInt(2, 1);
                } else if (args[3].equalsIgnoreCase("false")) {
                    value.setColor(new Color(43, 167, 60));
                    value.setTitle("Successfully updated value : join_message_enabled");
                    value.setDescription("The value \"join_message_enabled\" has been updated to false.");
                    value.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    preparedStatement.setInt(2, 0);
                } else {
                    value.setColor(new Color(208, 51, 51));
                    value.setTitle("Not a valid argument.");
                    value.setDescription("You can only use \"true\" or \"false\".");
                    value.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    event.getChannel().sendMessage(value.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    return;
                }
                event.getChannel().sendMessage(value.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                preparedStatement.execute();
                return;
            case "join_role_enabled":
                EmbedBuilder value1 = new EmbedBuilder();
                PreparedStatement preparedStatement1 = Main.getPlugin().getConnection()
                        .prepareStatement("UPDATE guild_data SET guild_id = ?, join_role_enabled = ?");
                preparedStatement1.setLong(1, event.getGuild().getIdLong());
                value1.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                if (args[3].equalsIgnoreCase("true")) {
                    value1.setColor(new Color(43, 167, 60));
                    value1.setTitle("Successfully updated value : join_role_enabled");
                    value1.setDescription("The value \"join_role_enabled\" has been updated to true.");
                    preparedStatement1.setInt(2, 1);
                } else if (args[3].equalsIgnoreCase("false")) {
                    value1.setColor(new Color(43, 167, 60));
                    value1.setTitle("Successfully updated value : join_role_enabled");
                    value1.setDescription("The value \"join_role_enabled\" has been updated to false.");
                    preparedStatement1.setInt(2, 0);
                } else {
                    value1.setColor(new Color(208, 51, 51));
                    value1.setTitle("Not a valid argument.");
                    value1.setDescription("You can only use \"true\" or \"false\".");
                    event.getChannel().sendMessage(value1.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    return;
                }
                event.getChannel().sendMessage(value1.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                preparedStatement1.execute();
                return;
            case "join_role_id":
                try {

                    PreparedStatement preparedStatement2 = Main.getPlugin().getConnection()
                            .prepareStatement("UPDATE guild_data SET guild_id = ?, join_role_id = ?");
                    preparedStatement2.setLong(1, event.getGuild().getIdLong());

                    long id = Long.parseLong(args[3]);
                    if (event.getGuild().getRoleById(id)==null) {
                        EmbedBuilder em1 = new EmbedBuilder();
                        em1.setColor(new Color(208, 51, 51));
                        em1.setTitle("Not a valid argument.");
                        em1.setDescription("You can only use role ids.");
                        event.getChannel().sendMessage(em1.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                        return;
                    }
                    preparedStatement2.setLong(2, id);
                    preparedStatement2.execute();

                    EmbedBuilder em = new EmbedBuilder().setTitle("Successfully updated value : join_role_id").setDescription("The value \"join_role_id\" has been set to " + id+".").setColor(new Color(43, 167, 60)).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    event.getChannel().sendMessage(em.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    return;
                }catch (NumberFormatException e) {
                    EmbedBuilder em1 = new EmbedBuilder();
                    em1.setColor(new Color(208, 51, 51));
                    em1.setTitle("Not a valid argument.");
                    em1.setDescription("You can only use role ids.\n**WARNING** This will only work if the bot has a higher role than the role you are assigning at join!!!");
                    event.getChannel().sendMessage(em1.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    return;
                }
        }

    }

    @Override
    public void cancel() {

    }
}
