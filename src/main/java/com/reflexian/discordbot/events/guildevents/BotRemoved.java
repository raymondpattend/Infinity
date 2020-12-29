package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.events.threads.MySQLThread;
import com.reflexian.discordbot.mysql.MySQL;
import com.reflexian.discordbot.utilities.objects.Server;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BotRemoved extends ListenerAdapter {

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        MySQL.dropEntry("guild_data", "guild_id", event.getGuild().getId());
        Server.SERVER_MAP.remove(event.getGuild().getIdLong());
        MySQLThread thread = new MySQLThread();
        thread.start();
        thread=null;
        try {
            PreparedStatement ps = Main.getPlugin().getConnection()
                    .prepareStatement("SELECT * FROM user_data WHERE user_key LIKE '%" + event.getGuild().getIdLong() + "%'");
            ResultSet rs =ps.executeQuery();
            Statement s = Main.getPlugin().getConnection().createStatement();
            while (rs.next()) {
                s.addBatch("DELETE FROM user_data WHERE user_key='"+rs.getString("user_key")+"';");
            }
            s.executeBatch();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Infinity was removed from " + event.getGuild().getName());
    }
}
