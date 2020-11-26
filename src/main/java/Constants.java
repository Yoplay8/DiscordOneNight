import discord4j.core.object.entity.MessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

public class Constants {
    // This is my id.
    public static final String ADMIN_ID = "322870637309984770";

    //Bot commands
    public static final String PREFIX = "!";
    public static final String COMMAND_SET_PLAYERS = "setPlayers";
    public static final String COMMAND_PLAY = "play";
    public static final String COMMAND_SET_DECK = "setDeck";
    public static final String COMMAND_DISPLAY_DECK = "displayDeck";
    public static final String COMMAND_DISPLAY_PLAYING_DECK = "displayPlayingDeck";

    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 13;// Can make the max based off of deck created?
    public static final int MAX_CARDS = 16;
    public static int numberOfPlayers = 0;

    public static Map<Integer, Consumer<MessageCreateSpec>> allCards = Collections.synchronizedMap(new HashMap<>());
    public static Map<Integer, Consumer<MessageCreateSpec>> tempAllCards = Collections.synchronizedMap(new HashMap<>());


    //public static Consumer<MessageCreateSpec>[] allCards = new Consumer[MAX_CARDS];
    public static LinkedList<InputStream> playingCards = new LinkedList<>();

    public static String[] CARD_NAMES = {"Werewolf", "Werewolf", "Minon", "Mason", "Mason", "Seer", "Robber", "Troublemaker", "Drunk", "Insomniac", "Villager", "Villager", "Villager", "Hunter", "Tanner", "Doppelganger"};


    public static Flux<MessageChannel> holdEvent;
    public static LinkedList<String> tmpPlayingDeck = new LinkedList<String>();
    public static final String tmpCOMMAND_ADD_CARD = "add";
    public static final String tmpCOMMAND_REMOVE_CARD = "remove";

    public static Queue<Disposable> events = new LinkedList<>();
}
