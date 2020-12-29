package com.reflexian.discordbot.events.runnables;

import com.reflexian.discordbot.Main;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.SQLException;

public class PlayerCounter implements Runnable {

    private boolean doStop = false;

    private int num = 0;


    public synchronized void doStop() {
        this.doStop = true;
    }

    private synchronized boolean keepRunning() {
        return !this.doStop;
    }


    @Override
    public void run() {
        while (keepRunning()) {
            if (num == 0) {
                int i = 0;
                for (Guild guild : Main.getJda().getGuilds()) {
                    i+=guild.getMemberCount();
                }
                Main.getJda().getPresence().setActivity(Activity.playing("with " + i + " users | @Infinity#9833 help"));
                num=1;
            } else if (num==1) {
                Main.getJda().getPresence().setActivity(Activity.playing("with " + (Main.getJda().getGuilds().size()) + " guilds | @Infinity#9833 help"));
                num=2;
            } else if (num==2) {
                Main.getJda().getPresence().setActivity(Activity.playing("@Infinity#9833 help"));
                try {
                    Main.getPlugin().executeQuery("SELECT * FROM guild_data", true);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                num=0;
            }

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
