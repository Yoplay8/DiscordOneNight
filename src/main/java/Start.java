import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public class Start {

    public static void main(String args[]) {
        String SECRET = "Nzc4Nzc4MzkwMjkwNzU5NzIz.X7W75Q._8XBrJDQ4tuy8pbEvNQFIGIsJAo";
        DiscordClient client = DiscordClientBuilder.create(SECRET).build();
        Setup setup = new Setup(client);

        //setup.cutImages();
        setup.eventSetNumberOfPlayers();
        setup.tmpEventDisplayPlayingDeck(null, null);
        setup.tempAddCard();
        setup.tempRemoveCard();
        setup.eventDisplayAllCards();

//        client.getEventDispatcher().on(ReadyEvent.class)
//                .subscribe(event -> {
//                    User self = event.getSelf();
//                    System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
//                });

        client.login().block();
    }
}
