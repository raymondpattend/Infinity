package com.reflexian.discordbot.utilities;

import com.reflexian.discordbot.Main;
import net.dv8tion.jda.api.JDA;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Files {

    JDA jda = Main.getJda();
    Main main = Main.getPlugin();

    public void saveFiles() throws IOException {
        Main.logger.info("Z");
        for (DiscordUser discordUser : Main.discordUserMap.values()) {

            try {
                PreparedStatement statement = main.getConnection()
                        .prepareStatement("SELECT * FROM users WHERE ID=?");
                Main.logger.info("A");
                statement.setLong(1, discordUser.getId());
                ResultSet results = statement.executeQuery();
                if (!results.next()) { // TRUE WE HAVE FOUND IT! \ OTHERWISE FALSE WE HAVEN'T FOUND IT
                    Main.logger.info("B");
                    PreparedStatement insert = main.getConnection()
                            .prepareStatement("INSERT INTO users (ID,CAPTCHA) VALUES (?,?)");
                    insert.setLong(1, discordUser.getId());
                    insert.setString(2, discordUser.getCaptchaCode());
                    insert.executeUpdate();

                    Main.logger.info("Saved value " + discordUser.getUser().getAsTag());
                }
                Main.logger.info("C");
            } catch (SQLException e) {
                e.printStackTrace();
                Main.logger.info("D");
            }
        }
    }
}
