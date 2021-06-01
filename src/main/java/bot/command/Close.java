package bot.command;

import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author kroko
 * @created on 18.01.2021 : 23:37
 */
public class Close implements Command {

    @Override
    public String command() {
        return "close";
    }

    @Override
    public String description() {
        return "close a Ticket";
    }

    @Override
    public Permission requiredPermission() {
        return null;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        final TextChannel textChannel = (TextChannel) event.getChannel();
        assert event.getMember() != null;
        if(textChannel.getName().startsWith("ticket") && Objects.requireNonNull(event.getTextChannel().getParent()).getName().equalsIgnoreCase("Support")) {

            Collection<Permission> collection = new ArrayList<>();
            collection.add(Permission.VIEW_CHANNEL);
            collection.add(Permission.MESSAGE_WRITE);

            List<Category> categoryList = event.getGuild().getCategoriesByName("Old Tickets", true);
            Category oldTickets = null;
            for (Category category : categoryList) {
                if (oldTickets == null)
                    oldTickets = category;
            }
            textChannel.getManager().setParent(oldTickets).queue();

            textChannel.getMemberPermissionOverrides().forEach(permissionOverride -> {
                permissionOverride.delete().queue();
            });
        }
    }
}
