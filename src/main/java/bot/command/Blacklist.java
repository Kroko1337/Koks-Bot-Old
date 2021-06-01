package bot.command;

import bot.Main;
import bot.manager.Setting;
import bot.manager.command.Command;
import bot.manager.file.FileManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

/**
 * @author kroko
 * @created on 07.02.2021 : 19:34
 */
public class Blacklist implements Command {
    @Override
    public String command() {
        return "blacklist";
    }

    @Override
    public String description() {
        return "add or remove words from a blacklist";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) throws IOException {
        final Setting setting = Main.getSettings(event.getGuild());
        assert setting != null;
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "add":
                case "a":
                    if(!setting.BLACKLISTED_WORDS.contains(args[1])) {
                        setting.BLACKLISTED_WORDS.add(args[1]);
                        FileManager.saveGuild(setting.guild);
                        sendEmbed(event, "Added Word!", args[1] + " added to the list!", ColorType.SUCCESSFULLY.getColor());
                    }else{
                        sendEmbed(event, "Word is already in the Blacklist!", "", ColorType.ERROR.getColor());
                    }
                    break;
                case "remove":
                case "rem":
                case "r":
                    if(setting.BLACKLISTED_WORDS.contains(args[1])) {
                        setting.BLACKLISTED_WORDS.remove(args[1]);
                        FileManager.saveGuild(setting.guild);
                        sendEmbed(event, "Removed Word!", args[1] + " removed from the list!", ColorType.SUCCESSFULLY.getColor());
                    } else {
                        sendEmbed(event, "Word is not in the Blacklist!", "", ColorType.ERROR.getColor());
                    }
                    break;
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                String words = "";
                for (int i = 0; i < setting.BLACKLISTED_WORDS.size(); i++)
                    words += setting.BLACKLISTED_WORDS.get(i) + ", ";
                if (words.length() > 2)
                    words = words.substring(0, words.length() - 2);
                sendEmbed(event, "Forbidden Words", words, ColorType.INFO.getColor());
            }
        } else {
            sendEmbed(event, "Wrong Syntax", prefix + "blacklist add/remove/list {Word}", ColorType.ERROR.getColor());
        }
    }
}
