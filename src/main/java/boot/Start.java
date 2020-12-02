package boot;

import discord4j.core.DiscordClient;

public class Start {
    public static void main(String args[]) {
        DiscordClient client = new Bot().bootBot();
        new Commands(client);

        client.login().block();
    }
}
