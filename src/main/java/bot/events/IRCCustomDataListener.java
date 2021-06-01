package bot.events;

import bot.Main;
import bot.manager.Setting;
import de.liquiddev.ircclient.api.CustomDataListener;
import de.liquiddev.ircclient.client.IrcPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;

import java.nio.charset.StandardCharsets;

public class IRCCustomDataListener implements CustomDataListener {

    @Override
    public void onCustomDataReceived(IrcPlayer ircPlayer, String s, byte[] bytes) {
        if(s.equalsIgnoreCase("requestKoksInvite")) {
            final String nick = ircPlayer.getIrcNick();
            final String clName = ircPlayer.getExtra();
            final String info = new String(bytes, StandardCharsets.UTF_8);
            final String[] split = info.split(":");

            for(Guild guild : Main.jda.getGuilds()) {
                final Setting setting = Main.getSettings(guild);
                if (setting != null) {
                    boolean isOnServer = false;
                    for(User user : guild.getJDA().getUsers()) {
                        if(setting.KOKS_USER.get(user.getId()) != null && setting.KOKS_USER.get(user.getId()).equalsIgnoreCase(clName))
                            isOnServer = true;
                    }
                    if (!isOnServer && clName != null && split.length == 2 && ircPlayer.getExtra().equalsIgnoreCase(split[0]) && ircPlayer.getIrcNick().equalsIgnoreCase(split[1]))
                    {
                        final Invite invite = guild.getTextChannels().get(0).createInvite().complete();
                        setting.INVITES.put(invite.getCode(), clName.toLowerCase());
                        Main.ircClient.sendCustomData("sendKoksInvite", invite.getUrl().getBytes(), IrcPlayer.getByIrcNickname(nick));
                    }
                    System.out.println("Received Data from " + ircPlayer.getIrcNick() + " (" + ircPlayer.getExtra() + ") " + info + " (" + isOnServer + ")");
                }
            }
        }
    }
}
