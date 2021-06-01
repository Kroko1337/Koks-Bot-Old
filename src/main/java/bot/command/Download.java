package bot.command;

import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;

/**
 * @author kroko
 * @created on 25.02.2021 : 22:21
 */
public class Download implements Command {

    @Override
    public String command() {
        return "download";
    }

    @Override
    public String description() {
        return "you can download the pictures from users";
    }

    @Override
    public Permission requiredPermission() {
        return null;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) throws IOException {
        if(args.length == 1) {
            final Member member = event.getGuild().getMemberById(args[0]);
            if(member != null) {

            }
        }
    }
}
