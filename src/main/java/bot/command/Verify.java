package bot.command;

import bot.Main;
import bot.manager.Setting;
import bot.manager.command.Command;
import bot.manager.file.FileManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.regex.Pattern;

public class Verify implements Command {

    @Override
    public String command() {
        return "verify";
    }

    @Override
    public String description() {
        return "verify other people";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) throws IOException {
        if (args.length == 2) {
            final Guild guild = event.getGuild();
            final Setting settings = Main.getSettings(guild);
            if (settings != null) {
                String userId = "";
                for (int i = 0; i < args[0].length(); i++) {
                    String cur = args[0].substring(i, i + 1);
                    if (Pattern.matches("[0-9]", cur))
                        userId += cur;
                }

                final Setting setting = Main.getSettings(event.getGuild());
                if (setting != null) {
                    final Member member = event.getGuild().getMemberById(userId);
                    if (member != null)
                        if (setting.KOKS_USER.containsKey(member.getId()) && !member.getUser().getName().contains("✓")) {
                            event.getGuild().modifyNickname(member, member.getUser().getName() + " ✓").queue();
                        }
                }

                settings.KOKS_USER.put(userId, args[1].toLowerCase());
                sendEmbed(event, "Verified", "You verified " + args[1], ColorType.SUCCESSFULLY.getColor());
                FileManager.saveGuild(guild);
            }
        }
    }
}
