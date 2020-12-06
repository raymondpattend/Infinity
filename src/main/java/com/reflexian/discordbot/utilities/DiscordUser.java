package com.reflexian.discordbot.utilities;

import net.dv8tion.jda.api.entities.User;

public class DiscordUser {

    private long id;

    private User user;

    private String captchaCode;


    public DiscordUser(long discordID) {
        this.id = discordID;
    }

    public long getId() {
        return id;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {

        this.user = user;
    }
}
