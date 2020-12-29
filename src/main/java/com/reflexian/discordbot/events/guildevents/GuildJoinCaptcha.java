package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.mysql.MySQL;
import com.reflexian.discordbot.utilities.Captcha;
import com.reflexian.discordbot.utilities.UtilStrings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuildJoinCaptcha extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        if (event.getMember().getUser().isBot()) return;
        if (!event.getGuild().getId().equals("770142850633433094")) return;

        String code = UtilStrings.generateRandomChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 5);
        MySQL.setString("user_data", "verification_code", code, "user_key",event.getMember().getId()+"#770142850633433094");

        Captcha captcha = new Captcha(150, 50);
        captcha.background(new Color(91, 132, 215));
        captcha.text(code);
        captcha.noiseCurvedLine();
        //captcha.distortion();
        BufferedImage image = captcha.getImage();

        File file = new File("temp.png");
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        EmbedBuilder message = new EmbedBuilder();
        message.setColor(new Color(91, 132, 215));
        message.setTitle("Welcome to " + event.getGuild().getName() + "!");
        message.setThumbnail(event.getGuild().getIconUrl());
        message.setDescription("Please send the captcha code here.\n\nIn order to continue, you must pass this captcha.\n**NOTE:** This is **Case-Sensitive**.");
        message.addField("", "**Your captcha:**", false);
        message.setImage("attachment://temp.png");
        message.setFooter("Contact a moderator if something goes wrong.", event.getMember().getUser().getAvatarUrl()).setTimestamp(new Date().toInstant());
        System.out.println("Sent captcha to " + event.getMember().getUser().getAsTag()+".");
        event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendFile(file, "temp.png").embed(message.build()).queue();
        });

    }


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType().isGuild()||event.getAuthor().isBot()) return;
        String key=MySQL.getString("user_data", "verification_code", "user_key", event.getAuthor().getIdLong()+"#770142850633433094");
        if (key==null||key.equals("verified")) {
            event.getPrivateChannel().sendMessage("Sorry, but I do not accept commands in DMs. Please use my commands in a server!").queue();
            return;
        }
        if (event.getMessage().getContentRaw().equals(key)) {
            System.out.println(event.getAuthor().getAsTag() + " completed the captcha.");
            event.getPrivateChannel().sendMessage(new EmbedBuilder().setTitle("Verified").setDescription("You have successfully passed the captcha! Enjoy yourself in our server \\:)").setColor(new Color(30, 114, 161)).build()).queue();
            Role role = event.getJDA().getGuildById(770142850633433094L).getRoleById(770172614790086677L);
            event.getJDA().getGuildById(770142850633433094L).addRoleToMember(event.getAuthor().getId(), role).queue();
            MySQL.setString("user_data", "verification_code", "verified", "user_key",event.getAuthor().getId()+"#770142850633433094");
            return;
        }
        event.getPrivateChannel().sendMessage(new EmbedBuilder().setTitle("Wrong Code").setDescription("That is the wrong code! Try again. \\:(").setColor(new Color(158, 47, 47)).build()).queue();
    }

     /*@Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("o")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(new Color(91, 132, 215));
            embedBuilder.setThumbnail(event.getGuild().getIconUrl());
            embedBuilder.setTitle("Welcome...");
            embedBuilder.setDescription("I see you haven't verified yet. You won't gain access to this discord until you do so, but it's simple!\n\nCheck your DMs for a message from me :)");
            embedBuilder.addField("Didn't get a message?", "This could be caused by 2 things...", false);
            embedBuilder.addField("1. I'm offline...", "If I'm offline, there is nothing you can do but wait. My devs are working hard to get me back online. Additionally you can contact staff to manually add the role to you.", false);
            embedBuilder.addField("2. You have DMs disabled.", "If you have DMs disabled, please enable them just this once! User Settings > Privacy and Safety > Allow direct messages from server members. Then simply create an invite, copy it, leave and rejoin this discord server!", false);
            embedBuilder.addField("3. None of these worked.", "If none of those options worked for you, then please Private Message a staff (seen by their MODERATOR) role.", false);
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }
    }*/

    /*Captcha captcha = new Captcha(150, 50);
        captcha.background(new Color(91, 132, 215));
        captcha.text(this.generateRandomChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789", 5));
        captcha.noiseCurvedLine();
        captcha.distortion();
        BufferedImage image = captcha.getImage();

        File file = new File("temp.png");
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        EmbedBuilder message = new EmbedBuilder();
        message.setColor(new Color(91, 132, 215));
        message.setTitle("Welcome to " + event.getGuild().getName() + "!");
        message.setDescription("Please send the captcha code here.\n\nIn order to continue, you must pass this captcha.\n**NOTE:** This is **Case-Sensitive**.\n⠀");
        message.addField("", "**Your captcha:**   3 Attempts Remaining **"+captcha.getText()+"**", false);
        message.setImage("attachment://temp.png");
        message.setFooter("You can request a new captcha using !captcha", event.getMember().getUser().getAvatarUrl());

        event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendFile(file, "temp.png").embed(message.build()).queue();
            codes.put(event.getUser(), captcha.getText());
        });
        Captcha.captchaMemberMap.put(event.getMember(), captcha.getText());
    }

    private String generateRandomChars(String candidateChars, int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(candidateChars.charAt(random.nextInt(candidateChars
                    .length())));
        }

        return sb.toString();
    }*/
}
