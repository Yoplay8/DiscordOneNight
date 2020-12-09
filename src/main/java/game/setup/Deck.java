package game.setup;

import common.Constants;
import common.Filter;
import discord4j.core.DiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.function.Consumer;


public class Deck extends Filter {
    DiscordClient client;
    String holdMessage;
    InputStream inputStream;
    Consumer<MessageCreateSpec> messageCreateSpecConsumer;
    Constants constants = new Constants();

    public Deck(DiscordClient client) {
        super(client);
        this.client = client;
    }

    public void subscribeSetPlayers() {
        Flux<Message> adminAndCommandEvent = isAdminAndCommand(constants.SET_PLAYERS);
        adminAndCommandEvent.flatMap(Message::getChannel)
                .flatMap(channel -> {
                    try {
                        constants.numberOfPlayers = Integer.valueOf(getContent());
                    } catch(Exception e) {
                        return channel.createMessage("Unable to set the number of players");
                    }
                    if(constants.numberOfPlayers < constants.MIN_PLAYERS || constants.numberOfPlayers > constants.MAX_PLAYERS) {
                        return channel.createMessage("Set number of players within range " + constants.MIN_PLAYERS + " - " + constants.MAX_PLAYERS);
                    }
                    return channel.createMessage("Total players set to " + constants.numberOfPlayers);
                })
                .subscribe();
    }

    public void subscribeAndCutImage() {
        URL res = getClass().getClassLoader().getResource("Cards.png");
        BufferedImage originalImgage = null;
        Flux<Message> adminAndCommandEvent = isAdminAndCommand(constants.DISPLAY_UNUSED);

        try {
            originalImgage = ImageIO.read(Paths.get(res.toURI()).toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int oWidth = originalImgage.getWidth();
        int width = oWidth / 4;// 4 == Columns in image
        int height = originalImgage.getHeight() / 4;// 4 == Rows in image
        int tWidth = 0;
        int tHeight = 0;
        BufferedImage subImgage;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        for(String cardName : constants.CARD_NAMES) {
            if (tWidth == oWidth) {
                tWidth = 0;
                tHeight += height;
                break;// << tmp code ------------------
            }

            subImgage = originalImgage.getSubimage(tWidth, tHeight, width, height);
            try {
                ImageIO.write(subImgage, "png", byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Card card = new Card(cardName, new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), adminAndCommandEvent.subscribe());
            card.setSubscribedEvent(resubscribeCardsMessage(adminAndCommandEvent, card));
            constants.unusedCards.put(cardName, card);
            byteArrayOutputStream.reset();

            tWidth+=width;
        }
    }

    public void subscribeAddToDeck() {
        Flux<Message> messageFlux = isAdminAndCommand(constants.ADD);
        messageFlux.flatMap(Message::getChannel)
            .flatMap(messageChannel -> {
                String message;
                String[] names = getContent().split(" ");

                if(addCards(names)) {
                    message = constants.unusedCards.size() == 0 ? "Added cards to deck. All unused cards added to playing deck" : "Added cards to deck.";
                } else {
                    message = "Couldn't find cards";
                }
                messageCreateSpecConsumer = messageCreateSpec -> messageCreateSpec.setContent(message);

                return messageChannel.createMessage(messageCreateSpecConsumer);
            })
            .subscribe();
    }

    private boolean addCards(String[] cardNames) {
        boolean flag = false;
        Flux<Message> adminAndCommandEvent = isAdminAndCommand(constants.DISPLAY_PLAYING_DECK);

        for(String cardName : cardNames) {
            Object[] cardCollection = constants.unusedCards.get(cardName).toArray();
            Card card = (cardCollection.length > 0) ? (Card) cardCollection[0] : null;

            if (card != null) {
                constants.unusedCards.removeMapping(card.getCardName(), card);
                card.setSubscribedEvent(resubscribeCardsMessage(adminAndCommandEvent, card));
                constants.playingCards.put(card.getCardName(), card);

                flag = true;
            }
        }
        return flag;
    }

    public void subscribeRemoveFromDeck() {
        Flux<Message> messageFlux = isAdminAndCommand(constants.REMOVE);
        messageFlux.flatMap(Message::getChannel)
                .flatMap(messageChannel -> {
                    String message;
                    String[] names = getContent().split(" ");

                    if(removeCard(names)) {
                        message = constants.unusedCards.size() == 0 ? "Removed cards from deck. All playing cards removed from playing deck" : "Removed cards from deck.";
                    } else {
                        message = "Couldn't find cards";
                    }
                    messageCreateSpecConsumer = messageCreateSpec -> messageCreateSpec.setContent(message);

                    return messageChannel.createMessage(messageCreateSpecConsumer);
                })
                .subscribe();
    }

    private boolean removeCard(String[] cardNames) {
        boolean flag = false;
        Flux<Message> adminAndCommandEvent = isAdminAndCommand(constants.DISPLAY_UNUSED);

        for(String cardName : cardNames) {
            Object[] cardCollection = constants.playingCards.get(cardName).toArray();
            Card card = (cardCollection.length > 0) ? (Card) cardCollection[0] : null;

            if (card != null) {
                constants.playingCards.removeMapping(card.getCardName(), card);
                card.setSubscribedEvent(resubscribeCardsMessage(adminAndCommandEvent, card));
                constants.unusedCards.put(card.getCardName(), card);

                flag = true;
            }
        }
        return flag;
    }

    private Disposable resubscribeCardsMessage(Flux<Message> adminAndCommandEvent, Card card) {
        card.getSubscribedEvent().dispose();

        return adminAndCommandEvent.flatMap(Message::getChannel)
            .flatMap(messageChannel -> {
                messageCreateSpecConsumer = messageCreateSpec -> messageCreateSpec.setContent(card.getCardName()).addFile(card.getCardName() + ".png", card.getInputStream());
                card.getInputStream().reset();

                return messageChannel.createMessage(messageCreateSpecConsumer);
            })
            .subscribe();
    }
}
