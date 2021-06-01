package bot.manager.command;

import bot.command.*;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author kroko
 * @created on 18.01.2021 : 19:47
 */
public class CommandManager {

    private final ArrayList<Command> COMMANDS = new ArrayList<>();

    public static final String PREFIX = ".";

    public CommandManager() {
        addCommand(new Support());
        addCommand(new Close());
        addCommand(new Mute());
        addCommand(new Unmute());
        addCommand(new Help());
        addCommand(new Nuke());
        addCommand(new Settings());
        addCommand(new Spam());
        addCommand(new Exit());
        addCommand(new ID());
        addCommand(new Blacklist());
        addCommand(new Ban());
        addCommand(new Verify());
        getCommands().sort(Comparator.comparing(Command::command));
    }

    public void addCommand(Command command) {
        COMMANDS.add(command);
    }

    public ArrayList<Command> getCommands() {
        return COMMANDS;
    }
}
