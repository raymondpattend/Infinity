package com.reflexian.discordbot.commands.administrative;

import com.reflexian.discordbot.listeners.Command;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Leveling extends Command {
    public Leveling(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        Server server = Server.getServer(event.getGuild());
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            sendMessage(event.getTextChannel(), new EmbedBuilder().setTitle("No permission.").setDescription("You need ``MANAGE_SERVER`` permission to use this command!\nTrying to get your level? Use ``@Infinity#9388 rank``").setFooter("Issued by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl()).setColor(new Color(189, 55, 55)).build(), 10);
            return;
        }
        if (args.length < 3) {
            EmbedBuilder help = new EmbedBuilder().setColor(new Color(189, 55, 55));
            help.setTitle("Help - Leveling");
            help.setDescription("Sub Commands start with <@775250061504413727> leveling");
            help.addField("Commands", "Enable/Disable **-** Enable/Disable leveling in this guild\nSetMessage **-** Set the levelup message\nGetMessage **-** Get the current levelup message\nSetChannel **-** Set the channel in which levelup messages are sent\nGetChannel **-** Get the channel in which levelup messages are sent\nAddRole **-** Add role to level rewards\nRemoveRole **-** Remove role from level rewards\nListRoles **-** List the role rewards", false);
            help.addField("Description", "Leveling Commands allow you to change how leveling works in your guild. You are able to change the levelup message, enable/disable leveling, and more.", false);
            help.addField("Permission", "Requires ``MANAGE_SERVER`` to execute subcommands.", false);
            help.addField("Example", "```@Infinity#9388 leveling setmessage Congrats! You have leveled up: %old% **->** %new%.```", false);
            help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
            sendMessage(event.getTextChannel(), help.build(), 60);
            return;
        }
        if (args.length == 3) {
            switch (args[2].toLowerCase()) {
                case "enable":
                    if (server.getSettings().isLevel_enabled()){
                        server.getSettings().setLevel_enabled(true);
                        sendMessage(event.getTextChannel(), this.valueSet("Leveling Enabled", true, false).build(), 60);
                    } else {
                        sendMessage(event.getTextChannel(), this.valueSet("Leveling Enabled", true, true).build(), 60);
                    }
                    return;
                case "disable":
                    if (!server.getSettings().isLevel_enabled()) {
                        server.getSettings().setLevel_enabled(false);
                        sendMessage(event.getTextChannel(), this.valueSet("Leveling Enabled", false, false).build(), 60);
                    } else {
                        sendMessage(event.getTextChannel(), this.valueSet("Leveling Enabled", false, true).build(), 60);
                    }
                    return;
                case "message":
                case "getmessage":
                    EmbedBuilder value = new EmbedBuilder().setTitle("Current Level Up Message").setDescription("```" + server.getSettings().getLevel_message() + "```\n``%usermention%`` **-** The mention of the user\n``%usertag%`` **-** The tag of the user\n``%previous%`` **-** The previous level number\n``%new%`` **-** The new level number").setColor(new Color(36, 70, 128)).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), value.build(), 60);
                    return;
                case "listroles":
                    Map<Integer, Long> roleToIntegerMap = new HashMap<>();
                    for (String string : server.getSettings().getLevel_roles().split(",")) {
                        try {
                            int a;
                            if (string.split(":")[0].equals("none")) a = 0;
                            else a = Integer.parseInt(string.split(":")[0]);
                            Long b = Long.parseLong(string.split(":")[1]);
                            roleToIntegerMap.put(a, b);
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                    }
                    EmbedBuilder roleValues = new EmbedBuilder().setTitle("Level Up Roles").setDescription("These are the levels assigned to users when they level up to a certain level.").setColor(new Color(49, 112, 189)).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    if (roleToIntegerMap.size() == 0) {
                        roleValues.addField("No Roles Found", "This guild doesn't have any saved role rewards!", false);
                    } else {
                        roleToIntegerMap.forEach((key, value2) -> {
                            try {
                                roleValues.addField(event.getGuild().getRoleById(value2) == null ? "Invalid Role ID" : event.getGuild().getRoleById(value2).getName() + " - LVL. " + key, event.getGuild().getRoleById(value2).getId(), false);
                            }catch (NullPointerException e) {
                                roleValues.addField("Invalid Role - " + value2, "This role was most likely deleted. Remember to remove it from Infinity's leveling rewards!", false);
                            }
                        });
                    }
                    sendMessage(event.getTextChannel(), roleValues.build(), 40);
                    return;
                case "getchannel":
                    TextChannel textChannel = server.getSettings().getLevel_channel() == 0 ? null : event.getGuild().getTextChannelById(server.getSettings().getLevel_channel());
                    EmbedBuilder value3 = new EmbedBuilder().setTitle("Current Level Up Channel").setDescription("" + (textChannel == null ? "No valid channel has been set!" : textChannel.getAsMention())).setColor(new Color(36, 70, 128)).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), value3.build(), 60);
                    return;
                case "setmessage":
                    EmbedBuilder helpSetMessage = new EmbedBuilder().setColor(new Color(189, 55, 55)).setTitle("Help - Leveling;SetMessage").addField("Correct Usage", "```@Infinity#9833 leveling setmessage <value>```", false).addField("Example", "```@Infinity#9833 leveling setmessage %usermention% leveled up to %new%!!!```", false).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), helpSetMessage.build(), 30);
                    return;
                case "addrole":
                    EmbedBuilder helpAddRole = new EmbedBuilder().setColor(new Color(189, 55, 55)).setTitle("Help - Leveling;AddRole").addField("Correct Usage", "```@Infinity#9833 leveling addrole <level number> <roleID>```", false).addField("Example", "```@Infinity#9833 leveling addrole 1 728779878144409681```", false).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), helpAddRole.build(), 30);
                    return;
                case "removerole":
                    EmbedBuilder helpRemoveRole = new EmbedBuilder().setColor(new Color(189, 55, 55)).setTitle("Help - Leveling;RemoveRole").addField("Correct Usage", "```@Infinity#9833 leveling removerole <roleID>```", false).addField("Example", "```@Infinity#9833 leveling removerole 728779878144409681```", false).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), helpRemoveRole.build(), 30);
                    return;
                default:
                    EmbedBuilder help = new EmbedBuilder().setColor(new Color(189, 55, 55));
                    help.setTitle("Help - Leveling");
                    help.setDescription("Sub Commands start with <@775250061504413727> leveling");
                    help.addField("Commands", "Enable/Disable **-** Enable/Disable leveling in this guild\nSetMessage **-** Set the levelup message\nGetMessage **-** Get the current levelup message\nSetChannel **-** Set the channel in which levelup messages are sent\nGetChannel **-** Get the channel in which levelup messages are sent\nAddRole **-** Add role to level rewards\nRemoveRole **-** Remove role from level rewards\nListRoles **-** List the role rewards", false);
                    help.addField("Description", "Leveling Commands allow you to change how leveling works in your guild. You are able to change the levelup message, enable/disable leveling, and more.", false);
                    help.addField("Permission", "Requires ``MANAGE_SERVER`` to execute subcommands.", false);
                    help.addField("Example", "```@Infinity#9388 leveling setmessage Congrats! You have leveled up: %old% **->** %new%.```", false);
                    help.setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), help.build(), 60);
                    return;
            }
        } else if (args.length == 4) {
            switch (args[2].toLowerCase()) {
                case "setmessage":
                    server.getSettings().setLevel_message(args[3]);
                    sendMessage(event.getTextChannel(), this.valueSet("Level Up Message", "```" + args[3] + "```", false).build(), 40);
                    return;
                case "addrole":
                    EmbedBuilder helpAddRole = new EmbedBuilder().setColor(new Color(189, 55, 55)).setTitle("Help - Leveling;AddRole").addField("Correct Usage", "```@Infinity#9833 leveling addrole <level number> <roleID>```", false).addField("Example", "```@Infinity#9833 leveling addrole 1 728779878144409681```", false).setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), helpAddRole.build(), 30);
                    return;
                case "setchannel":
                    if (event.getMessage().getMentionedChannels().size() == 0) {
                        EmbedBuilder failed = new EmbedBuilder().setColor(new Color(185, 55, 55)).setTitle("Invalid Channel").setDescription("You did not include a valid channel mention.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                        sendMessage(event.getTextChannel(), failed.build(), 40);
                        return;
                    }
                    server.getSettings().setLevel_channel(event.getMessage().getMentionedChannels().get(0).getIdLong());
                    sendMessage(event.getTextChannel(), this.valueSet("Level Up Channel",  event.getMessage().getMentionedChannels().get(0).getName(), false).build(), 60);
                    return;
                case "removerole":

                    try {

                            StringBuilder roles = new StringBuilder().append(server.getSettings().getLevel_roles());
                            if (roles.toString().contains(args[3])&&args[3].length()==18) {
                                Map<Integer, Long> roleToIntegerMap = new HashMap<>();
                                StringBuilder newValue = new StringBuilder();
                                for (String string : server.getSettings().getLevel_roles().split(",")) {
                                    try {
                                        int a;
                                        if (string.split(":")[0].equals("none")) a = 0;
                                        else a = Integer.parseInt(string.split(":")[0]);
                                        Long b = Long.parseLong(string.split(":")[1]);
                                        roleToIntegerMap.put(a, b);
                                    } catch (ArrayIndexOutOfBoundsException ignored) {
                                    }
                                }
                                roleToIntegerMap.forEach((key, value) -> {
                                    if (value.equals(Long.parseLong(args[3]))) {}
                                    else newValue.append(key).append(":").append(value).append(",");
                                });
                                if (newValue.toString().split(":").length == 0|| newValue.toString().equals("")) server.getSettings().setLevel_roles("none");
                                else server.getSettings().setLevel_roles(newValue.toString());
                                EmbedBuilder success = new EmbedBuilder().setColor(new Color(37, 116, 59)).setTitle("Success!").setDescription("Successfully removed role with id **" + args[3] + ".").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                                sendMessage(event.getTextChannel(), success.build(), 60);
                            } else {
                                EmbedBuilder failed = new EmbedBuilder().setColor(new Color(185, 55, 55)).setTitle("Not a leveling reward").setDescription("The role ID you inputted is not a leveling reward.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                                sendMessage(event.getTextChannel(), failed.build(), 25);
                            }
                            return;
                    }catch (NumberFormatException e) {
                        EmbedBuilder failed = new EmbedBuilder().setColor(new Color(185, 55, 55)).setTitle("Invalid Numbers").setDescription("You can only include numbers for the value!").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                        sendMessage(event.getTextChannel(), failed.build(), 25);
                        return;
                    }
            }
        }
        switch (args[2].toLowerCase()) {
            case "setmessage":
                StringBuilder st = new StringBuilder();
                for (String string : args) {
                    if (string.equals(args[0]) || string.equals(args[1]) || string.equals(args[2])) continue;
                    st.append(string).append(" ");
                }
                server.getSettings().setLevel_message(st.toString());
                sendMessage(event.getTextChannel(), this.valueSet("Level Up Message", "```" + st.toString() + "```", false).build(), 40);
                return;
            case "addrole":

                // 3 LEVEL / 4 ROLE ID
                try {
                    int a = Integer.parseInt(args[3]);
                    long b = Long.parseLong(args[4]);
                    if (event.getGuild().getRoleById(b) == null) {
                        EmbedBuilder failed = new EmbedBuilder().setColor(new Color(185, 55, 55)).setTitle("Invalid Role ID").setDescription("The role ID you inputted is not a valid role in this discord. Please try again.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                        sendMessage(event.getTextChannel(), failed.build(), 40);
                        return;
                    }
                    StringBuilder roles = new StringBuilder().append(server.getSettings().getLevel_roles());
                    if (roles.toString().contains("," + a + ":")) {
                        EmbedBuilder failed = new EmbedBuilder().setColor(new Color(185, 55, 55)).setTitle("Level Reward already set!").setDescription("The level you would like the role to be applied to is already set. You can remove the role using ``@Infinity#9833 leveling rolelist`` and find the role ID in question.").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                        sendMessage(event.getTextChannel(), failed.build(), 40);
                        return;
                    }
                    roles.append(",").append(a).append(":").append(b);
                    server.getSettings().setLevel_roles(roles.toString().replace("none,",""));
                    EmbedBuilder success = new EmbedBuilder().setColor(new Color(37, 116, 59)).setTitle("Success!").setDescription("Successfully added role **" + event.getGuild().getRoleById(b).getName() + "** for level **" + a + "**!").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), success.build(), 40);
                } catch (NumberFormatException e) {
                    EmbedBuilder failed = new EmbedBuilder().setColor(new Color(185, 55, 55)).setTitle("Invalid Numbers").setDescription("You can only include numbers for the first and second value!").setFooter("Executed by " + event.getAuthor().getAsTag(), event.getAuthor().getAvatarUrl());
                    sendMessage(event.getTextChannel(), failed.build(), 25);
                }
        }
    }

    @Override
    public void cancel() {

    }

    private EmbedBuilder errorEmbed = new EmbedBuilder().setTitle("Something went wrong :(").setDescription("Sorry! Something went wrong on my end and the developers have been notified! Expect a fix within 12 hours.").setColor(new Color(144, 39, 39));

    private EmbedBuilder valueSet(String value, boolean valueboolean, boolean failed) {
        EmbedBuilder em = new EmbedBuilder();

        if (failed) {
            em.setColor(new Color(165, 48, 48));
            em.setTitle("Failed to update value");
            em.setDescription("The value \"" + value+"\" is already " + valueboolean+"!");
        } else {
            em.setColor(new Color(29, 120, 12));
            em.setTitle("Updated Value - " + value);
            em.setDescription("The value \"" + value+"\" has been updated to " + valueboolean+".");
        }
        return em;
    }

    private EmbedBuilder valueSet(String value, String valuestring, boolean failed) {
        EmbedBuilder em = new EmbedBuilder();

        if (failed) {
            em.setColor(new Color(165, 48, 48));
            em.setTitle("Failed to update value");
            em.setDescription("The value \"" + value+"\" is already \"" + valuestring+"\"!");
        } else {
            em.setColor(new Color(29, 120, 12));
            em.setTitle("Updated Value - " + value);
            em.setDescription("The value \"" + value+"\" has been updated to... \"" + valuestring +"\".");
        }
        return em;
    }




}
