package bot.events;

import bot.Main;
import bot.manager.Setting;
import de.liquiddev.ircclient.api.IrcPacketListener;
import de.liquiddev.ircclient.net.IrcPacket;
import de.liquiddev.ircclient.net.packet.Irc04AddPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class IRCEvent implements IrcPacketListener {

    @Override
    public void onReceived(IrcPacket ircPacket) {

        if(ircPacket instanceof Irc04AddPlayer) {
            final Irc04AddPlayer addPlayer = (Irc04AddPlayer) ircPacket;
            final String clientType = addPlayer.clientType;
            final String clientVersion = addPlayer.clientVersion;
            final String ircName = addPlayer.ircName;
            final String extra = addPlayer.extra;
            for (Guild guild : Main.jda.getGuilds()) {
                final Setting setting = Main.getSettings(guild);
                for (TextChannel channel : guild.getTextChannelsByName("irc", true)) {
                    final Message message = channel.sendMessage(ircName + " (" + extra + (setting != null && setting.KOKS_USER.containsValue(extra.toLowerCase()) ? " ✓" : "") + ") connected with " + clientType + " (" + clientVersion + ")").complete();
                }
            }
        }
    }

    @Override
    public void onSend(IrcPacket ircPacket) {

    }
}
