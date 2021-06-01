package bot.command;

import bot.Main;
import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author kroko
 * @created on 06.02.2021 : 22:57
 */
public class Exit implements Command {
    @Override
    public String command() {
        return "exit";
    }

    @Override
    public String description() {
        return "shutdown the bot";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if(args.length == 0) {
            System.exit(0);
        } else if(args.length == 1) {
            int id = Integer.parseInt(args[0]);
            if(Main.botID == id)
                System.exit(0);
        }
    }
}
