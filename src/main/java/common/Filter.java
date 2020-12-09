package common;

import boot.Main;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import lombok.Getter;
import reactor.core.publisher.Flux;

public class Filter {
  private Properties properties;
  private Constants constants;
  @Getter private String content;
  private DiscordClient client;

  protected Filter(DiscordClient client) {
    this.client = client;
    constants = new Constants();
    properties = new Properties();
    InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("Valut.properties");

    try {
      properties.load(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected Flux<Message> isAdminAndCommand(String command) {
    return client
        .getEventDispatcher()
        .on(MessageCreateEvent.class)
        .map(MessageCreateEvent::getMessage)
        .filter(
            message -> {
              return isAdmin(message) && isCommand(message, command);
            });
  }

  private boolean isAdmin(Message message) {
    return message
        .getAuthor()
        .map(user -> user.getId().asString().equals(properties.getProperty("my.id")))
        .orElse(false);
  }

  private boolean isCommand(Message message, String command) {
    Optional<String> holdMessage = message.getContent();
    if (holdMessage.orElse("").toLowerCase().startsWith(command)) {
      content =
          holdMessage.get().replace(holdMessage.get().substring(0, command.length()), "").trim();
      content = content.replace("  ", " ");
      return true;
    }
    return false;
  }
}
