package bot.command;

import bot.manager.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

/**
 * @author kroko
 * @created on 19.01.2021 : 15:58
 */
public class Nuke implements Command {

    @Override
    public String command() {
        return "nuke";
    }

    @Override
    public String description() {
        return "you can nuke the channel";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.MESSAGE_MANAGE;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) {
        final String name = event.getTextChannel().getName();
        final Category category = event.getMessage().getCategory();
        final int position = event.getTextChannel().getPosition();
        final TextChannel original = event.getTextChannel();
        final TextChannel textChannel = Objects.requireNonNull(event.getTextChannel()).createCopy().complete();
        original.delete().queue();
        textChannel.getManager().setPosition(position).queue();
        textChannel.sendMessage("Nuked the Channel!\nhttps://imgur.com/LIyGeCR").queue();
    }
}
