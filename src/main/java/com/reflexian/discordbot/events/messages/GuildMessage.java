package com.reflexian.discordbot.events.messages;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.mysql.MySQL;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GuildMessage extends ListenerAdapter {

    public static Map<Member, Long> levelcooldownMap = new HashMap<>();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        Server server = Server.getServer(event.getGuild());

        if (event.getAuthor().isBot()) return;
        if (!Main.fullyEnabled) return;
        if (!server.getSettings().isLevel_enabled()) return;


        if (levelcooldownMap.containsKey(event.getMember())) {

            if (((System.currentTimeMillis() - levelcooldownMap.get(event.getMember())) / 1000) < 60) return;
            levelcooldownMap.remove(event.getMember());
        }
        String message = event.getMessage().getContentRaw();
        if (message.startsWith("<@775250061504413727> ") || message.startsWith("<@!775250061504413727> ") || message.startsWith("<@!784515176132378625> ") || message.startsWith("<@784515176132378625> "))return;
        if (message.length() < 3) return;
        System.out.println("A");
        try {
            ResultSet playerdata = Main.getPlugin().executeQuery("SELECT * FROM user_data WHERE user_key = '" + event.getAuthor().getId()+"#"+event.getGuild().getId()+"';", true);
            if (playerdata.next()) {

                long xp = playerdata.getLong("leveling_xp");
                long level = playerdata.getLong("leveling_level");
                long xpneeded = playerdata.getLong("leveling_xpneeded");
                System.out.println("B " + xp + " " + level + " " + xpneeded);

                xp+=new Random().nextInt(30-10+1)+10;
                if (xp > xpneeded) {
                    System.out.println("C " + xp + " " + level + " " + xpneeded);
                    while (xp>=xpneeded) {
                        System.out.println("D " + xp + " " + level + " " + xpneeded);
                        xp-=xpneeded;
                        level+=1;
                        xpneeded=level*100;

                        if (server.getSettings().getLevel_channel()==0) {
                            sendMessage(event.getChannel(), server.getSettings().getLevel_message().replace("%usermention%", event.getAuthor().getAsMention()).replace("%usertag%", event.getAuthor().getAsTag()).replace("%previous%", (level-1+"")).replace("%new%", level+"").replace("%now%", level+""), null);
                        } else {
                            TextChannel textChannel = event.getGuild().getTextChannelById(server.getSettings().getLevel_channel());
                            sendMessage(textChannel == null ? event.getChannel() : textChannel, server.getSettings().getLevel_message().replace("%usermention%", event.getAuthor().getAsMention()).replace("%usertag%", event.getAuthor().getAsTag()).replace("%previous%", (level-1)+"").replace("%new%", level+"").replace("%now%", level+""), null);

                            Map<Integer, Long> roleToIntegerMap = new HashMap<>();
                            for (String string : server.getSettings().getLevel_roles().split(",")) {
                                try {
                                    int a;
                                    if (string.split(":")[0].equals("none")) a = 0;
                                    else a = Integer.parseInt(string.split(":")[0]);
                                    roleToIntegerMap.put(a,Long.parseLong(string.split(":")[1]));
                                } catch (ArrayIndexOutOfBoundsException ignored) {
                                }
                            }
                            if (roleToIntegerMap.size() != 0) {
                                if (roleToIntegerMap.containsKey(Integer.parseInt(level+""))) {
                                    Role role = event.getGuild().getRoleById(roleToIntegerMap.get(Integer.parseInt(level+"")));
                                    if (role!=null) {
                                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                                    }
                                }
                            }
                        }
                    }
                    levelcooldownMap.put(event.getMember(), System.currentTimeMillis());
                    Main.getPlugin().executeQuery("UPDATE user_data SET leveling_xp = " + xp + ", leveling_xpneeded = "+xpneeded+", leveling_level = "+level+" WHERE user_key = '" + event.getAuthor().getId() + "\\#" + event.getGuild().getId() + "';", true);
                    return;
                }
                Main.getPlugin().executeQuery("UPDATE user_data SET leveling_xp = " + xp+" WHERE user_key = '" + event.getAuthor().getId() + "\\#" + event.getGuild().getId() + "';", true);
            } else {
                MySQL.createMember(event.getMember(), event.getGuild());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
    public void sendMessage(TextChannel textChannel, String text, @Nullable Integer secondDelete) {
        if (textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.MESSAGE_WRITE)&&textChannel.getGuild().getSelfMember().hasPermission(textChannel, Permission.VIEW_CHANNEL)) {
            textChannel.sendMessage(text).queue(message -> {
                if (secondDelete==null) return;
                message.delete().queueAfter(secondDelete, TimeUnit.SECONDS);
            });
        }
    }
}
