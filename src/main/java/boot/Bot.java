package boot;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Bot {
    public DiscordClient bootBot() {
        Properties properties = new Properties();
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("Valut.properties");

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return DiscordClientBuilder.create(properties.getProperty("bot.secret")).build();
    }
}
