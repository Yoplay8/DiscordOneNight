import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public class Start {

    public static void main(String args[]) {
        DiscordClient client = DiscordClientBuilder.create("Nzc4Nzc4MzkwMjkwNzU5NzIz.X7W75Q.cdqRrI8wQfgXB3Zks9c5zeQdT6E").build();
        Setup setup = new Setup(client);

        setup.eventSetNumberOfPlayers();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                });

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().orElse("").equalsIgnoreCase("a"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(""))
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> {
                    System.out.println(message.getAuthor().get().getId() + " +++ " + message.getId());
                    return !user.isBot();}).orElse(false))
                .filter(message -> message.getContent().orElse("").equalsIgnoreCase("a"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("abc"))
                .subscribe();

        client.login().block();
    }
}
