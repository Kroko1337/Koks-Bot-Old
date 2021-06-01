package bot.command;

import bot.Main;
import bot.manager.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Ban implements Command {
    @Override
    public String command() {
        return "ban";
    }

    @Override
    public String description() {
        return "ban other users";
    }

    @Override
    public Permission requiredPermission() {
        return Permission.BAN_MEMBERS;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event) throws IOException {
        if(args.length == 1) {
            String userId = "";
            for (int i = 0; i < args[0].length(); i++) {
                String cur = args[0].substring(i, i + 1);
                if (Pattern.matches("[0-9]", cur))
                    userId += cur;
            }

            final Member member = event.getGuild().getMemberById(userId);
            assert member != null;

            if (!member.getPermissions().contains(Permission.ADMINISTRATOR) && !member.isOwner()) {
                final BufferedImage image = ImageIO.read(new File("assets/ejected.jpg"));
                final Font font = new Font("Arial", Font.PLAIN, 18);
                final Graphics graphics = image.getGraphics();
                final FontMetrics metrics = graphics.getFontMetrics(font);
                graphics.setFont(font);
                graphics.setColor(Color.WHITE);
                final String text = member.getUser().getName() + " was ejected";
                graphics.drawString(text, image.getWidth() / 2 - metrics.stringWidth(text) / 2, image.getHeight() / 2 + image.getHeight() / 3);
                final File file = new File("bann-" + System.currentTimeMillis() + ".png");
                ImageIO.write(image, "png", file);
                final String url = Main.uploadImage(file);
                file.delete();
                System.out.println(url);
                final EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("You banned " + member.getUser().getName());
                eb.setColor(ColorType.ERROR.getColor());
                eb.setImage(url);
                event.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(20, TimeUnit.SECONDS));
                member.ban(0).complete();
            } else {
                Main.sendEmbed(event, "You can't ban a moderator!", "", ColorType.ERROR.getColor(), 20, TimeUnit.SECONDS);
            }
        }
    }
}
