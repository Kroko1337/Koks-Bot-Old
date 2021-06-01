package bot.events;

import bot.Main;
import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * @author kroko
 * @created on 19.01.2021 : 01:46
 */
public class BlacklistEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String text = event.getMessage().getContentRaw();
        if (!Main.paused)
            if (!isAllowed(text, event)) {
                event.getMessage().delete().queue();
                Main.sendEmbed(event, "That's not allowed here!", "", new Color(255, 0, 54));
            }
        super.onMessageReceived(event);
    }

    public boolean isAllowed(String text, MessageReceivedEvent event) {
        if(event.getTextChannel().isNSFW())
            return true;
        if(event.getMember() != null && event.getMember().getPermissions().contains(Permission.ADMINISTRATOR))
            return true;
        String unformattedText = "";
        for (int i = 0; i < text.length(); i++) {
            String cur = text.substring(i, i + 1);
            if (Pattern.matches("[a-zA-Z ]", cur))
                unformattedText += cur;
        }

        boolean isValidCommand = false;

        final String[] args = text.split(" ");
        for (Command command : Main.COMMAND_MANAGER.getCommands())
            if (command.command().equalsIgnoreCase(args[0].replace(".", "")))
                isValidCommand = true;
        for (String black : Objects.requireNonNull(Main.getSettings(event.getGuild())).BLACKLISTED_WORDS) {
            if (unformattedText.equalsIgnoreCase(black) && !(text.startsWith(".") && isValidCommand)) {
                System.out.println(Objects.requireNonNull(event.getMember()).getEffectiveName() + " failed to write: " + black);
                return false;
            } else {
                final String[] splitWords = unformattedText.split(" ");
                for(String word : splitWords) {
                    if(word.equalsIgnoreCase(black)) {
                        return false;
                    }
                }

                for(String word : splitWords) {
                    if(word.toLowerCase().contains(black.toLowerCase()) && word.length() <= black.length() + 1) {
                        try {
                            Main.staffAlert(event.getMember(), "staff alerted word: " + black + "\nwrote: " + text, event.getGuild());
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return true;
    }
}
