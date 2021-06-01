package bot.command;

import bot.Main;
import bot.manager.Setting;
import bot.manager.command.Command;
import bot.manager.file.FileManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author kroko
 * @created on 19.01.2021 : 17:39
 */
public class Settings implements Command {
    @Override
    public String command() {
        return "settings";
    }

    @Override
    public String description() {
        return "you can change the settings from the bot";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 0) {
            final Setting setting = Main.getSettings(event.getGuild());
            assert setting != null;
            String settingList = "";
            for (String settings : setting.settings.keySet()) {
                Object value = setting.settings.get(settings);
                settingList += settings + ": " + value + "\n";
            }
            settingList = settingList.substring(0, settingList.length() - 1);
            sendEmbed(event, "Settings", settingList, ColorType.INFO.getColor());
        } else if (args.length == 1) {
            final String settingName = args[0];
            final Setting setting = Main.getSettings(event.getGuild());
            assert setting != null;
            String settingList = "";
            for (String settings : setting.settings.keySet()) {
                if (settings.equalsIgnoreCase(settingName))
                    settingList = settings;
            }
            Object value = setting.settings.get(settingList);
            sendEmbed(event,"Settings: " + settingList + " is " + value, "", ColorType.INFO.getColor());
        } else if (args.length == 2) {
            final String settingName = args[0];
            final Object value = args[1];
            final Setting setting = Main.getSettings(event.getGuild());
            String settingToSet = null;
            for (String set : setting.settings.keySet())
                if (set.equalsIgnoreCase(settingName))
                    settingToSet = set;

            if (settingToSet != null) {
                setting.settings.put(settingToSet, value);
                sendEmbed(event,"Updated Setting: " + settingToSet + " to " + value, "", ColorType.SUCCESSFULLY.getColor());
                try {
                    FileManager.saveGuild(setting.guild);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                sendEmbed(event, "Settings: " + settingName + " not exist!", "", ColorType.ERROR.getColor());
            }
        }
    }
}
