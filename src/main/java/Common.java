import discord4j.core.object.entity.Message;

public class Common {
    public static boolean isAdmin(Message message) {
        return message.getAuthor().map(user -> user.getId().asString().equals(Constants.ADMIN_ID)).orElse(false);
    }

    public static boolean isCommand(Message message) {
        return message.getContent().orElse("").startsWith(Constants.PREFIX + Constants.COMMAND_DISPLAY_DECK);
    }
}
