package bot;

import bot.events.Events;
import bot.events.IRCCustomDataListener;
import bot.events.IRCEvent;
import bot.events.NSFWDetector;
import bot.manager.Setting;
import bot.manager.command.Command;
import bot.manager.command.CommandManager;
import bot.manager.file.FileManager;
import bot.utils.ColorUtil;
import de.liquiddev.ircclient.api.IrcClient;
import de.liquiddev.ircclient.api.SimpleIrcApi;
import de.liquiddev.ircclient.client.ClientType;
import de.liquiddev.ircclient.client.IrcClientFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author kroko
 * @created on 18.01.2021 : 19:29
 */
public class Main {

    public static final CommandManager COMMAND_MANAGER = new CommandManager();
    public static IrcClient ircClient;
    public static JDA jda;

    public static HashMap<String, Setting> SETTINGS = new HashMap<>();

    public static int botID;
    public static boolean paused = false;

    public static void main(String[] args) throws LoginException {
        String token = "ODQyNDcwODQ5MDEzNzQzNjc3.YJ1yHg.S7j8uy6XxkjhbSd3WOVG5zidOEg";
        final JDABuilder builder = JDABuilder.create(token, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_INVITES);
        builder.setToken(token);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.addEventListeners(new Events());
        //builder.addEventListeners(new BlacklistEvent());
        builder.addEventListeners(new NSFWDetector());
        jda = builder.build();
        paused = false;
        botID = ThreadLocalRandom.current().nextInt(0, 999999999);

        ircClient = IrcClientFactory.getDefault().createIrcClient(ClientType.QUERY, "pnZK4N47kGefRDZ2", "koksbot", "1.0");

        ircClient.getApiManager().registerApi(new SimpleIrcApi() {
            @Override
            public void addChat(String s) {
                sendToAll(ColorUtil.stripColor(s).replace("�", "->"));
            }
        });
        ircClient.getApiManager().registerPacketListener(new IRCEvent());
        ircClient.getApiManager().registerCustomDataListener(new IRCCustomDataListener());
        Runtime.getRuntime().addShutdownHook(new Thread(Main::onShutDown));
    }

    public static void onShutDown() {
        for(Guild guild : jda.getGuilds()) {
            final Setting setting = getSettings(guild);
            if(setting != null) {
                for(Invite invite : guild.retrieveInvites().complete()) {
                    invite.delete().queue();
                }
                setting.INVITES.clear();
                try {
                    FileManager.saveGuild(guild);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendToAll(String string) {
        for (Guild guild : jda.getGuilds()) {
            for (TextChannel channel : guild.getTextChannelsByName("irc", true)) {
                final Message message = channel.sendMessage(string).complete();
            }
        }
    }

    public static String uploadImage(File file) throws IOException {
        final String clientID = "be132a250c06947";
        final String secret = "fe14f133bbc71b3b57c24ebd816a07a938b47ff6";
        final String url = "https://api.imgur.com/3/image";
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Client-ID " + clientID);
        connection.setReadTimeout(100000);
        connection.connect();
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
        outputStreamWriter.write("image=" + toBase64(file));
        outputStreamWriter.flush();
        outputStreamWriter.close();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String imageURL = null;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            final String[] args = line.split(",");
            for (int i = 0; i < args.length; i++) {
                final String[] splitArgs = args[i].split(":");
                if (splitArgs[0].replace("\"", "").equalsIgnoreCase("link")) {
                    imageURL = splitArgs[1].replace("\"", "") + ":" + splitArgs[2].substring(0, splitArgs[2].length() - 2);
                    imageURL = imageURL.replace("\\/", "/");
                }
            }
        }
        bufferedReader.close();
        return imageURL;
    }

    public static void staffAlert(Member alerted, String message, Guild guild) throws ExecutionException, InterruptedException {
        staffAlert(alerted, message, alerted.getUser().getEffectiveAvatarUrl(), guild);
    }

    public static void staffAlert(Member alerted, String message, String image, Guild guild) throws ExecutionException, InterruptedException {
        for (Member member : guild.getMembers()) {
            if (member.getPermissions().contains(Permission.ADMINISTRATOR) && !member.getUser().isBot()) {
                try {
                    final EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Staff Alert -> " + alerted.getUser().getName());
                    eb.setColor(Command.ColorType.ERROR.getColor());
                    eb.setThumbnail(image);
                    eb.setFooter(message + "\n\nUserID: " + alerted.getId());
                    member.getUser().openPrivateChannel().complete().sendMessage(eb.build()).complete();
                } catch (ErrorResponseException ignored) {
                }
            }
        }
    }

    public static void sendEmbed(MessageReceivedEvent event, String title, String description, Color color) {
        sendEmbed(event, title, description, color, 20, TimeUnit.SECONDS);
    }

    public static void sendEmbed(MessageReceivedEvent event, String title, String description, Color color, long delay, TimeUnit timeUnit) {
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setColor(color);
        event.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(delay, timeUnit));
    }

    public static Setting getSettings(Guild guild) {
        for (String id : SETTINGS.keySet()) {
            if (guild.getId().equalsIgnoreCase(id))
                return SETTINGS.get(id);
        }
        return null;
    }

    private static String toBase64(File file) {
        try {
            byte[] b = new byte[(int) file.length()];
            FileInputStream fs = new FileInputStream(file);
            fs.read(b);
            fs.close();
            return URLEncoder.encode(DatatypeConverter.printBase64Binary(b), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
