package bot.command;

import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.regex.Pattern;

/**
 * @author kroko
 * @created on 19.01.2021 : 01:44
 */
public class Unmute implements Command {
    @Override
    public String command() {
        return "unmute";
    }

    @Override
    public String description() {
        return "You can unmute a user";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.VOICE_MUTE_OTHERS;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 1) {
            String userId = "";
            for (int i = 0; i < args[0].length(); i++) {
                String cur = args[0].substring(i, i + 1);
                if(Pattern.matches("[0-9]", cur))
                    userId += cur;
            }

            final Member member = event.getGuild().getMemberById(userId);

            Role frau = null;
            for (Role role : event.getGuild().getRolesByName("Frau", true))
                frau = role;
            assert frau != null;
            assert member != null;
            event.getGuild().removeRoleFromMember(userId, frau).queue();
            event.getChannel().sendMessage("Unmuted " + args[0] + "!").queue();
        }
    }
}

