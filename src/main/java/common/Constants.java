package common;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.*;

public class Constants {
    public static int numberOfPlayers = 0;
    public LinkedList<InputStream> unusedCards = new LinkedList<>();
    public LinkedList<InputStream> playingCards = new LinkedList<>();

    //Bot commands
    public final String PREFIX = "!";
    public final LinkedList<String> COMMANDS = new LinkedList<>();

    public final int MAX_CARDS = 16;
    public final int MIN_PLAYERS = 3;
    public final int MAX_PLAYERS = MAX_CARDS - 3;

    public String[] CARD_NAMES = {"Werewolf", "Werewolf", "Minon", "Mason", "Mason", "Seer", "Robber", "Troublemaker", "Drunk", "Insomniac", "Villager", "Villager", "Villager", "Hunter", "Tanner", "Doppelganger"};

    public Constants() {
        COMMANDS.add(PREFIX + "setplayers");
        COMMANDS.add(PREFIX + "play");
        COMMANDS.add(PREFIX + "setdeck");
        COMMANDS.add(PREFIX + "displaydeck");
        COMMANDS.add(PREFIX + "displayplayingdeck");
        COMMANDS.add(PREFIX + "add");
        COMMANDS.add(PREFIX + "remove");
    }
}
