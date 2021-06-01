package bot.command;

import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * @author kroko
 * @created on 06.02.2021 : 15:44
 */
public class Spam implements Command {
    @Override
    public String command() {
        return "spam";
    }

    @Override
    public String description() {
        return "you can spam people";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        if (args.length == 2) {
            final String id = args[0];
            final int amount = Integer.parseInt(args[1]);
            final User user = event.getGuild().getJDA().getUserById(id);
            if (user != null) {
                for (int i = 0; i < amount; i++)
                    user.openPrivateChannel().complete().sendMessage("<@" + user.getId() + ">").queue();
                sendEmbed(event, "Spamming " + user.getName(), "with " + amount + " messages!", ColorType.SUCCESSFULLY.getColor());
            } else {
                sendEmbed(event, "User not found!", "", ColorType.ERROR.getColor());
            }
        }
    }
}
