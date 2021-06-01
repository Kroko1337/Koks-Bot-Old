package bot.events;

import bot.Main;
import bot.manager.Setting;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;

/**
 * @author kroko
 * @created on 19.01.2021 : 14:59
 */
public class NSFWDetector extends ListenerAdapter {

    public static byte[] readStream(InputStream in) throws IOException {
        final ArrayList<Byte> bytes = new ArrayList<>();
        while (in.available() > 0) {
            bytes.add((byte) in.read());
        }
        final byte[] b = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            b[i] = bytes.get(i);
        }
        return b;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!Main.paused)
            try {
                final Setting settings = Main.getSettings(event.getGuild());
                if(settings != null) {
                    final boolean allowNSWF = Boolean.parseBoolean(settings.getValue("allowNSWF") + "");
                    final String apiKey = (String) settings.getValue("deepAI-ApiKey");
                    final boolean hasAPIKey = apiKey != null;
                    if (!event.getTextChannel().isNSFW() && !allowNSWF && hasAPIKey) {
                        final String text = event.getMessage().getContentRaw();
                        final String[] args = text.split(" ");
                        for (String message : args) {
                            if (message.startsWith("http")) {
                                checkImage(message, apiKey, event);
                            }
                        }

                        event.getMessage().getAttachments().forEach(attachment -> {
                            if (attachment.isImage()) {
                                checkImage(attachment.getUrl(), apiKey, event);
                            }
                        });
                    }
                }
            } catch (ClassCastException ignore) {
            }
        super.onMessageReceived(event);
    }

    public void checkImage(String imageUrl, String apiKey, MessageReceivedEvent event) {
        try {
            final String website = "https://api.deepai.org/api/nsfw-detector";
            final URL url = new URL(website);
            final URLConnection connection = url.openConnection();
            final HttpsURLConnection http = (HttpsURLConnection) connection;

            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            final Map<String, String> arguments = new HashMap<>();
            arguments.put("image", imageUrl);
            final StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : arguments.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            final byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.setRequestProperty("api-key", apiKey);
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

            final byte[] response = readStream(http.getInputStream());
            final String s = new String(response);
            final JSONObject jsonObject = new JSONObject(s);
            final JSONArray detections = jsonObject.getJSONObject("output").getJSONArray("detections");
            final double nsfwScore = jsonObject.getJSONObject("output").getDouble("nsfw_score");
            if (!detections.isEmpty() || nsfwScore > 0.2) {
                event.getMessage().delete().queue();
                Main.sendEmbed(event, "NSFW is here not allowed!", "", new Color(255, 0, 54));
                Main.staffAlert(event.getMember(), "posted a NSFW Image\nNSFW-Score: " + nsfwScore, imageUrl, event.getGuild());
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
        }
    }
}
