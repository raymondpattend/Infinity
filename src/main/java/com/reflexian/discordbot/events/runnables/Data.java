package com.reflexian.discordbot.events.runnables;

import com.reflexian.discordbot.utilities.objects.Server;

public class Data implements Runnable{

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
            for (Server server : Server.SERVER_MAP.values()){
                if (!server.isUpdateToDatabase())continue;
                server.getSettings().setMySQLValues();
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
