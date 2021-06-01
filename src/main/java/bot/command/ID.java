package bot.command;

import bot.Main;
import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

/**
 * @author kroko
 * @created on 09.02.2021 : 14:47
 */
public class ID implements Command {
    @Override
    public String command() {
        return "id";
    }

    @Override
    public String description() {
        return "show the id from the bot";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) throws IOException {
        sendEmbed(event, "ID: " + Main.botID, "", ColorType.INFO.getColor());
    }
}
