package com.reflexian.discordbot.commands.fun;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.reflexian.discordbot.listeners.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;

public class Joke extends Command {
    public Joke(String[] command, @Nullable Member member, @Nullable User user) {
        super(command, member, user);
    }

    public long cooldown;

    @Override
    public void execute(MessageReceivedEvent event) throws SQLException {
        if ( ((System.currentTimeMillis()- cooldown)/1000) < 3) {
            sendMessage(event.getTextChannel(), "That command is on cooldown!", 5);
            return;
        }
        sendMessage(event.getTextChannel(), getJokeFromWeb(event.getAuthor().getName().replace(" ", "_")), 60);
    }

    private String getJokeFromWeb(String username) {
        try {
            URL loginurl = new URL("http://api.icndb.com/jokes/random?firstName=&lastName=" + username);
            URLConnection yc = loginurl.openConnection();
            cooldown=System.currentTimeMillis();
            yc.setConnectTimeout(3000);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine = in.readLine();
            JsonParser parser = new JsonParser();
            JsonObject array = parser.parse(inputLine).getAsJsonObject();
            return array.get("value").getAsJsonObject().get("joke").getAsString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public void cancel() {

    }
}
