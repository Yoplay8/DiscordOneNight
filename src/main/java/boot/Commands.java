package boot;

import discord4j.core.DiscordClient;
import game.setup.Deck;

public class Commands {
  private DiscordClient client;
  private Deck deck;

  /*
      COMMANDS.add(PREFIX + "play");
      COMMANDS.add(PREFIX + "displayplayingdeck");
  */

  Commands(DiscordClient client) {
    this.client = client;
    this.deck = new Deck(this.client);

    subscribeCommands();
  }

  private void subscribeCommands() {
    deck.subscribeAndCutImage();
    deck.subscribeAddToDeck();
    deck.subscribeRemoveFromDeck();
    deck.subscribeSetPlayers();
  }
}
