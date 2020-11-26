import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Flux;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class Setup {
    DiscordClient client;
    String holdMessage;
    InputStream inputStream;
    static AtomicInteger index = new AtomicInteger(0);
    Consumer<MessageCreateSpec> messageCreateSpecConsumer;

    String name;

    public Setup(DiscordClient client) {
        this.client = client;
    }

    public void eventSetNumberOfPlayers() {
        System.out.println("In Set Player");
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> {
                    return message.getAuthor().map(user -> user.getId().asString().equals(Constants.ADMIN_ID)).orElse(false);
                })
                .filter(message -> {
                    holdMessage = message.getContent().get();
                    return message.getContent().orElse("").startsWith(Constants.PREFIX + Constants.COMMAND_SET_PLAYERS);
                })
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    try {
                        Constants.numberOfPlayers = Integer.valueOf(holdMessage.replace(Constants.PREFIX + Constants.COMMAND_SET_PLAYERS, "").trim());
                        if(Constants.numberOfPlayers < Constants.MIN_PLAYERS || Constants.numberOfPlayers > Constants.MAX_PLAYERS) {
                            return channel.createMessage("Set number of players within range " + Constants.MIN_PLAYERS + " - " + Constants.MAX_PLAYERS);
                        }
                        return channel.createMessage("Total players set to " + Constants.numberOfPlayers);
                    } catch(Exception e) {
                        return channel.createMessage("Unable to set the number of players");
                    }
                })
                .subscribe();
    }

    public void tempAddCard() {
        System.out.println("In Add");
        client.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .filter(message -> {
                return message.getAuthor().map(user -> user.getId().asString().equals(Constants.ADMIN_ID)).orElse(false);
            })
            .filter(message -> {
                System.out.println("In Event Add");
                if(message.getContent().orElse("").startsWith(Constants.PREFIX + Constants.tmpCOMMAND_ADD_CARD)) {
                    Constants.tmpPlayingDeck.add(holdMessage.replace(Constants.PREFIX + Constants.tmpCOMMAND_ADD_CARD, "").trim());
                    System.out.println(Arrays.toString(Constants.CARD_NAMES));
                    Constants.CARD_NAMES[2] = "???????";
                    System.out.println(Arrays.toString(Constants.CARD_NAMES));
                }
                else
                    System.out.println("No Good");

                for(String card : Constants.tmpPlayingDeck) {
                    System.out.println("In playing deck: " + card);
                }

                return message.getContent().orElse("").startsWith(Constants.PREFIX + Constants.tmpCOMMAND_ADD_CARD);
            })
            .flatMap(Message::getChannel)
            .subscribe();
    }

    public void tempRemoveCard() {
        System.out.println("In Delete");
        client.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .filter(message -> {
                return message.getAuthor().map(user -> user.getId().asString().equals(Constants.ADMIN_ID)).orElse(false);
            })
            .filter(message -> {
                if(message.getContent().orElse("").startsWith(Constants.PREFIX + Constants.tmpCOMMAND_REMOVE_CARD))
                    Constants.tmpPlayingDeck.remove(holdMessage.replace(Constants.PREFIX + Constants.tmpCOMMAND_REMOVE_CARD, "").trim());

                return message.getContent().orElse("").startsWith(Constants.PREFIX + Constants.tmpCOMMAND_ADD_CARD);
            })
            .subscribe();
    }

    public void tmpEventDisplayPlayingDeck(Flux<MessageChannel> holdEvent, String card) {
        if(holdEvent != null) {
            System.out.println("In Playing");
            Flux<MessageChannel> tmp = client.getEventDispatcher().on(MessageCreateEvent.class)
                    .map(MessageCreateEvent::getMessage)
                    .filter(message -> {
                        System.out.println("In Filter1 Playing");
                        return message.getAuthor().map(user -> user.getId().asString().equals(Constants.ADMIN_ID)).orElse(false);
                    })
                    .filter(message -> {
                        System.out.println("In Filter2 Playing");

                        return message.getContent().orElse("").startsWith(Constants.PREFIX + Constants.COMMAND_DISPLAY_PLAYING_DECK);
                    })
                    .flatMap(Message::getChannel);

            for(String tmpCard : Constants.tmpPlayingDeck) {
                createMessage(tmp, tmpCard);
            }

        }
    }

    private Flux<Message> createMessage(Flux<MessageChannel> holdEvent, String card) {
        return holdEvent.flatMap(messageChannel -> {
            return messageChannel.createMessage(card);
        });
    }

//    public void tmpEventDisplayDeck() {
//        System.out.println("In Display Deck");
//
//        client.getEventDispatcher().on(MessageCreateEvent.class)
//            .map(MessageCreateEvent::getMessage)
//            .filter(message -> {
//                return Common.isAdmin(message) && Common.isCommand(message);
//            })
//            .filter(message -> {
//                addMessageToQueue(message);
//                return true;
//            }).subscribe();
//    }

//    private void addMessageToQueue(Message eventMessage) {
//        while(!Constants.events.isEmpty()) {
//            System.out.println("-");
//            Constants.events.poll().dispose();
//        }
//
//        for (String name : Constants.CARD_NAMES) {
//            System.out.println("+");
//            Constants.events.add(eventMessage.getChannel().flatMap(channel -> {
//                System.out.println(name);
//                return channel.createMessage(name);
//            })
//            .subscribe());
//        }
//    }

    public void eventDisplayAllCards() {
        Flux<Message> holdEvent = client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> {
                    return Common.isAdmin(message);
                })
                .filter(message -> {
                    return message.getContent().orElse("").startsWith(Constants.PREFIX + Constants.COMMAND_DISPLAY_DECK);
                });

        cutImage();
        createMessages(holdEvent);
    }

    private void createMessages(Flux<Message> holdEvent) {
        for(InputStream stream : Constants.playingCards) {
            holdEvent.flatMap(Message::getChannel)
                    .flatMap(messageChannel -> {
                        messageCreateSpecConsumer = messageCreateSpec -> messageCreateSpec.setContent(name).addFile(name + ".png", stream);
                        return messageChannel.createMessage(messageCreateSpecConsumer);
                    })
                    .subscribe();
        }
    }

    private void cutImage() {
        URL res = getClass().getClassLoader().getResource("Cards.png");
        BufferedImage originalImgage = null;

        try {
            originalImgage = ImageIO.read(Paths.get(res.toURI()).toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int oWidth = originalImgage.getWidth();
        int oHeight = originalImgage.getHeight();
        int width = oWidth / 4;
        int height = oHeight / 4;
        int tWidth = 0;
        int tHeight = 0;
        BufferedImage subImgage;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        for(String name : Constants.CARD_NAMES) {
            if (tWidth == oWidth) {
                tWidth = 0;
                tHeight += height;

                if (tHeight == oHeight)
                    break;
            }

            subImgage = originalImgage.getSubimage(tWidth, tHeight, width, height);
            try {
                ImageIO.write(subImgage, "png", byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            Constants.playingCards.add(inputStream);

            byteArrayOutputStream = new ByteArrayOutputStream();

            tWidth+=width;
        }

        System.out.println(Constants.COMMAND_DISPLAY_DECK + " Command Complete");
    }
}
