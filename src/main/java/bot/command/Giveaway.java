package bot.command;

import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

/**
 * @author kroko
 * @created on 09.02.2021 : 14:59
 */
public class Giveaway implements Command {
    @Override
    public String command() {
        return "giveaway";
    }

    @Override
    public String description() {
        return "start giveaways";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.PRIORITY_SPEAKER;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) throws IOException {
        if(args.length >= 2) {
            final int days = Integer.parseInt(args[0]);
            String what = "";
            for(int i = 1; i < args.length; i++) {
                what += args[i] + " ";
            }
            what = what.substring(0, what.length() - 1);
        }
    }
}
