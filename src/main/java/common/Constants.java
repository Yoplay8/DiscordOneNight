package common;

import game.setup.Card;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

public class Constants {
  public int numberOfPlayers = 0;
  public MultiValuedMap<String, Card> playingCards = new ArrayListValuedHashMap<>();
  public MultiValuedMap<String, Card> unusedCards = new ArrayListValuedHashMap<>();

  // Bot commands
  public final String PREFIX = "!";
  public final String SET_PLAYERS = PREFIX + "setplayers";
  public final String DISPLAY_UNUSED = PREFIX + "displayunused";
  public final String DISPLAY_PLAYING_DECK = PREFIX + "displayplaying";
  public final String ADD = PREFIX + "add";
  public final String REMOVE = PREFIX + "remove";
  public final String PLAY = PREFIX + "play";

  public final int MAX_CARDS = 16;
  public final int MIN_PLAYERS = 3;
  public final int MAX_PLAYERS = MAX_CARDS - 3;

  public String[] CARD_NAMES = {
    "werewolf",
    "werewolf",
    "minon",
    "mason",
    "mason",
    "seer",
    "robber",
    "troublemaker",
    "drunk",
    "insomniac",
    "villager",
    "villager",
    "villager",
    "hunter",
    "tanner",
    "doppelganger"
  };
}
