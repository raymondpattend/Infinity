package com.reflexian.discordbot.events.guildevents;

import com.reflexian.discordbot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuildJoinCaptcha extends ListenerAdapter {

    private Map<User, String> codes = new HashMap<>();

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        if (event.getMember().getUser().isBot()) return;
        if (!event.getGuild().getId().equals("770142850633433094")) return;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(91, 132, 215));
        embedBuilder.setTitle("Welcome to " + event.getGuild().getName() + "!");
        embedBuilder.setDescription("In order to continue to our server, you must pass this simple captcha.\nSimply press the <:greenCheck:761713176882053190> emoji to verify!");
        embedBuilder.addField("Bot offline?", "If the bot is offline or nothing happens when you press <:greenCheck:761713176882053190>, contact a staff member.", false);
        embedBuilder.setThumbnail(event.getGuild().getIconUrl());
        embedBuilder.setFooter("Made by Reflexian LLC");
        event.getMember().getUser().openPrivateChannel().queue(privateChannel -> {

            privateChannel.sendMessage(embedBuilder.build()).queue(message -> {
                message.addReaction(":greenCheck:761713176882053190").queue();
            });
            Main.logger.info("Sent verification to " + event.getMember().getUser().getAsTag() + " in " + event.getGuild().getName());
        });

    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getChannelType().isGuild()||event.getUser().isBot()||Main.getJda().getGuildById("770142850633433094").getMemberById(event.getUserId())==null) return;
        event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {

            try {
                if (message.getAuthor().isBot()&&message.getEmbeds().get(0).getTitle().startsWith("Welcome to ")&&event.getReaction().getReactionEmote().getEmote().getName().equals("greenCheck")) {
                    JDA jda = Main.getJda();
                    if (Objects.requireNonNull(jda.getGuildById("770142850633433094")).getMemberById(event.getUserId()) != null) {
                        Main.getJda().getGuildById("770142850633433094").addRoleToMember(jda.getGuildById("770142850633433094").getMemberById(event.getUserId()), Objects.requireNonNull(event.getJDA().getRoleById("770172614790086677"))).queue();
                        event.getChannel().sendMessage("You have been verified!").queue();
                        Main.logger.info(event.getUser().getAsTag() + " has been verified in LostInSpace Support.");
                    }
                }
            } catch (IllegalStateException ignored) {}




        }, (failure) -> {
            if (failure instanceof ErrorResponseException) {
                ErrorResponseException ex = (ErrorResponseException) failure;
                if (ex.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                    event.getChannel().sendMessage("Something went wrong, try rejoining the server ;(").queue();
                }
            }
        });

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
        //TODO Remove the captcha result or else this whole thing is useless
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
