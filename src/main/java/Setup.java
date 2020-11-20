import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Setup {
    DiscordClient client;

    public Setup(DiscordClient client) {
        this.client = client;
    }

    public void eventSetNumberOfPlayers() {
        String imagePath = "Cards.png";
        BufferedImage myPicture;
        try {
            myPicture = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> {
                    return message.getAuthor().map(user -> user.getId().asString().equals(Constants.ADMIN_ID)).orElse(false);
                })
                .filter(message -> {
                    try {
                        Constants.numberOfPlayers = Integer.valueOf(message.getContent().get().toString().replace(Constants.PREFIX + Constants.COMMAND_SET_PLAYERS, "").trim());
                        return message.getContent().orElse("").startsWith(Constants.PREFIX + Constants.COMMAND_SET_PLAYERS);
                    } catch(Exception e) {
                        return true;
                    }
                })
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                        try {
                            if(Constants.numberOfPlayers < Constants.MIN_PLAYERS || Constants.numberOfPlayers > Constants.MAX_PLAYERS) {
                                return channel.createMessage("Set number of players within range " + String.valueOf(Constants.MIN_PLAYERS) + " - " + String.valueOf(Constants.MAX_PLAYERS));
                            }
                            return channel.createMessage("Total players set to " + String.valueOf(Constants.numberOfPlayers));
                        } catch(Exception e) {
                            return channel.createMessage("Unable to set the number of players");
                        }
                })
                .subscribe();
    }
}
