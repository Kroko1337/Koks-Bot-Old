package bot.manager.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author kroko
 * @created on 18.01.2021 : 19:46
 */
public interface Command {

    String prefix = CommandManager.PREFIX;

    String command();

    String description();

    Permission requiredPermission();

    void execute(String[] args, MessageReceivedEvent event) throws IOException;

    default void sendEmbed(MessageReceivedEvent event, String title, String description, Color color) {
        sendEmbed(event, title, description, color, 20, TimeUnit.SECONDS);
    }

    default void sendEmbed(MessageReceivedEvent event, String title, String description, Color color, long delay, TimeUnit timeUnit) {
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setDescription(description);
        eb.setColor(color);
        event.getChannel().sendMessage(eb.build()).queue( message -> message.delete().queueAfter(delay, timeUnit));
    }

    enum ColorType {
        SUCCESSFULLY(new Color(89, 255, 0)), ERROR(new Color(255, 0, 54)), INFO(new Color(0, 153, 255));

        Color color;

        ColorType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }
    
}
