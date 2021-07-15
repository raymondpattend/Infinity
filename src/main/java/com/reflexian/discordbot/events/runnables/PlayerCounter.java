package com.reflexian.discordbot.events.runnables;

import com.reflexian.discordbot.Main;
import com.reflexian.discordbot.events.log.MessageLoader;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

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

                OffsetDateTime o = OffsetDateTime.now().plusHours(1);
                MessageLoader.messageMap.values().forEach(e -> {

                    long diff = ChronoUnit.HOURS.between(e.getTimeCreated().toInstant(), Instant.now());
                    System.out.println("Difference: " + diff);

                    if (diff >= 1) {
                        MessageLoader.messageMap.remove(e.getIdLong());
                        Main.logger.info("Removed message with id " + e.getId());
                    }
                });

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
