package bot.command;

import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author kroko
 * @created on 18.01.2021 : 23:22
 */
public class Support implements Command {

    @Override
    public String command() {
        return "support";
    }

    @Override
    public String description() {
        return "you can open a ticket";
    }

    @Override
    public Permission requiredPermission() {
        return null;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        final List<Category> category = event.getGuild().getCategoriesByName("Support", true);
        Category support = null;
        for (Category cat : category) {
            if (support == null)
                support = cat;
        }
        assert support != null;
        String randomTicket = "ticket-" + ThreadLocalRandom.current().nextInt(0, 1871337911);
        while (!event.getGuild().getTextChannelsByName(randomTicket, true).isEmpty())
            randomTicket = "ticket-" + ThreadLocalRandom.current().nextInt(0, 1871337911);
        Collection<Permission> collection = new ArrayList<>();
        collection.add(Permission.VIEW_CHANNEL);
        collection.add(Permission.MESSAGE_WRITE);
        assert event.getMember() != null;
        final TextChannel channel = support.createTextChannel(randomTicket).addMemberPermissionOverride(event.getMember().getIdLong(), collection, null).complete();

        for(Member member : event.getGuild().getMembers()) {
            if (member.getPermissions().contains(Permission.BAN_MEMBERS)) {
                channel.createPermissionOverride(member).setAllow(collection).queue();
            }
        }
    }
}
