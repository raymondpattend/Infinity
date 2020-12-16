package com.reflexian.discordbot.events.leveling;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.mysql.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
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



        if (event.getAuthor().isBot()) return;
        if (!MySQL.getBool("guild_data", "level_enabled", "guild_id", event.getGuild().getId())) return;


        if (levelcooldownMap.containsKey(event.getMember())) {

            long a = (System.currentTimeMillis()-levelcooldownMap.get(event.getMember()))/1000;
            if (a<60) return;
            levelcooldownMap.remove(event.getMember());
        }
        String message = event.getMessage().getContentRaw();
        if (message.startsWith("<@775250061504413727> ")||message.startsWith("<@!775250061504413727> ")||message.startsWith("<@!784515176132378625> ")||message.startsWith("<@784515176132378625> ")) return;
        if (message.length() < 3) return;

        try {
            PreparedStatement preparedStatement = Main.getPlugin().getConnection()
                    .prepareStatement("SELECT * FROM user_data WHERE user_key = '"+ Objects.requireNonNull(event.getMember()).getId()+"#"+event.getGuild().getId()+"';");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                long xp = new Random().nextInt(30-10+1)+10;
                xp+=rs.getInt("leveling_xp");
                long level = rs.getLong("leveling_level");
                long maxXp = rs.getLong("leveling_xpneeded");
                if (xp>maxXp) {
                    xp=0;
                    level++;
                    maxXp=level*100;
                    PreparedStatement ps = Main.getPlugin().getConnection().prepareStatement("SELECT * FROM guild_data WHERE guild_id = " + event.getGuild().getId() + ";");
                    ResultSet resultSet = ps.executeQuery();
                    if (resultSet.next()) {
                        if (resultSet.getLong("level_channel")==0) {
                            sendMessage(event.getChannel(), resultSet.getString("level_message").replace("%usermention%", event.getAuthor().getAsMention()).replace("%usertag%", event.getAuthor().getAsTag()).replace("%previous%", (level-1)+"").replace("%new%", level+"").replace("%now%", level+""), null);
                        } else {
                            TextChannel textChannel = event.getGuild().getTextChannelById(resultSet.getLong("level_channel"));
                            sendMessage(textChannel == null ? event.getChannel() : textChannel, resultSet.getString("level_message").replace("%usermention%", event.getAuthor().getAsMention()).replace("%usertag%", event.getAuthor().getAsTag()).replace("%previous%", (level-1)+"").replace("%new%", level+"").replace("%now%", level+""), null);

                            Map<Integer, Long> roleToIntegerMap = new HashMap<>();
                            for (String string : resultSet.getString("level_roles").split(",")) {
                                try {
                                    int a;
                                    if (string.split(":")[0].equals("none")) a = 0;
                                    else a = Integer.parseInt(string.split(":")[0]);
                                    Long b = Long.parseLong(string.split(":")[1]);
                                    roleToIntegerMap.put(a, b);
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
                }
                levelcooldownMap.put(event.getMember(), System.currentTimeMillis());

                Statement statement = Main.getPlugin().getConnection().createStatement();
                statement.executeUpdate("UPDATE user_data SET leveling_xp = " + xp + ", leveling_xpneeded = "+maxXp+", leveling_level = "+level+" WHERE user_key = '" + event.getMember().getId() + "#" + event.getGuild().getId() + "';");
            } else {
                PreparedStatement ps = Main.getPlugin().getConnection().prepareStatement("INSERT IGNORE INTO user_data(user_key,leveling_level,leveling_xp,leveling_xpneeded) VALUES (?,0,"+new Random().nextInt(30-10+1)+ ",100)");
                ps.setString(1, event.getAuthor().getId() + "#"+event.getGuild().getId());
                ps.execute();
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
