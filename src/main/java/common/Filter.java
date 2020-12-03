package common;

import boot.Start;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import lombok.Getter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

public class Filter {
    private Properties properties;
    private Constants constants;
    @Getter
    private String content;
    private DiscordClient client;

    protected Filter(DiscordClient client) {
        this.client  = client;
        constants = new Constants();
        properties = new Properties();
        InputStream inputStream = Start.class.getClassLoader().getResourceAsStream("Valut.properties");

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected  void produceOneTimeCreateMessageEvent(String message) {
        client.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .flatMap(Message::getChannel)
            .flatMap(messageChannel -> {
                Consumer<MessageCreateSpec> messageCreateSpecConsumer = messageCreateSpec -> messageCreateSpec.setContent(message);

                return messageChannel.createMessage(messageCreateSpecConsumer);
            })
            .subscribe().dispose();
    }

    protected Flux<Message> isAdminAndCommand(String command) {
        return client.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .filter(message -> {
                return isAdmin(message) && isCommand(message, command);
            });
    }

    private boolean isAdmin(Message message) {
        return message.getAuthor().map(user -> user.getId().asString().equals(properties.getProperty("my.id"))).orElse(false);
    }

    private boolean isCommand(Message message, String command) {
        Optional<String> holdMessage = message.getContent();
        if(holdMessage.orElse("").toLowerCase().startsWith(command)) {
            content = holdMessage.get().replace(holdMessage.get().substring(0, command.length()), "").trim();
            content = content.replace("  ", " ");
            return true;
        }
        return false;
    }
}
