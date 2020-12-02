package common;

import boot.Start;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class Filter {
    private Properties properties;
    private Constants constants;
    private String commandInContent;
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

    protected Flux<Message> isAdminAndCommand() {
        return client.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage)
            .filter(message -> {
                return isAdmin(message);// && isCommand(message);
            });
    }

    private boolean isAdmin(Message message) {
        return message.getAuthor().map(user -> user.getId().asString().equals(properties.getProperty("my.id"))).orElse(false);
    }

    private boolean isCommand(Message message) {
        Optional<String> holdMessage = message.getContent();
        for(String command : constants.COMMANDS) {
            if(holdMessage.orElse("").toLowerCase().startsWith(command)) {
                commandInContent = command;
                content = holdMessage.get().replace(holdMessage.get().substring(0, command.length()), "").trim();

                return true;
            }
        }
        return false;
    }
}
