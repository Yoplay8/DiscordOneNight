package boot;

import discord4j.core.DiscordClient;

public class Main {
    public static void main(String args[]) {
        DiscordClient client = new Bot().bootBot();
        new Commands(client);

        client.login().block();
    }
}
