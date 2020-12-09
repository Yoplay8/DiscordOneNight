package game.setup;

import lombok.Data;
import reactor.core.Disposable;

import java.io.ByteArrayInputStream;

@Data
public class Card {
    private String cardName;
    private ByteArrayInputStream inputStream;
    private Disposable subscribedEvent;

    public Card(String cardName, ByteArrayInputStream inputStream, Disposable subscribedEvent) {
        this.cardName = cardName;
        this.inputStream = inputStream;
        this.subscribedEvent = subscribedEvent;
    }
}
