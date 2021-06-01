package bot.command;

import bot.Main;
import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author kroko
 * @created on 19.01.2021 : 11:34
 */
public class Help implements Command {
    @Override
    public String command() {
        return "help";
    }

    @Override
    public String description() {
        return "help pages for the commands";
    }

    @Override
    public Permission requiredPermission() {
        return null;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        
        final ArrayList<Command> allowedCommands = new ArrayList<>();
        
        for(Command command : Main.COMMAND_MANAGER.getCommands()) {
            if(command.requiredPermission() == null || Objects.requireNonNull(event.getMember()).getPermissions().contains(command.requiredPermission()))
                allowedCommands.add(command);
        }

        double sites = allowedCommands.size() / 10D;
        int sitesInt = (int) sites;
        if (sites > sitesInt)
            sitesInt++;
        
        if (args.length == 0) {
            String description = "";
            for (int i = 0; i < 10; i++) {
                if (allowedCommands.size() > i) {
                    final Command command = allowedCommands.get(i);
                    final String name = command.command();
                    final String commandDescription = command.description();
                    description += Main.COMMAND_MANAGER.PREFIX + name + " -> " + commandDescription + "\n";
                }
            }
            sendEmbed(event, "Help Site 1/" + sitesInt, description.substring(0, description.length() - 2), ColorType.SUCCESSFULLY.getColor());
        } else if (args.length == 1) {
            int number = Integer.parseInt(args[0]);

            if(number > sitesInt)
                number = sitesInt;

            String description = "";
            for (int i = (10 * (number - 1)); i < 10 * number; i++) {
                if (allowedCommands.size() > i) {
                    final Command command = allowedCommands.get(i);
                    final String name = command.command();
                    final String commandDescription = command.description();
                    description += Main.COMMAND_MANAGER.PREFIX + name + " -> " + commandDescription + "\n";
                }
            }
            description = description.substring(0, description.length() - 2);
            sendEmbed(event, "Help Site " + number + "/" + sitesInt, description, ColorType.SUCCESSFULLY.getColor());
        }
    }
}
