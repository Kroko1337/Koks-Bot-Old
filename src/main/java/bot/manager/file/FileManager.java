package bot.manager.file;

import bot.Main;
import bot.manager.Setting;
import net.dv8tion.jda.api.entities.Guild;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

/**
 * @author kroko
 * @created on 19.01.2021 : 17:31
 */
public class FileManager {

    public final static File GUILD = new File("save");

    public static void loadGuild(Guild guild) throws IOException {
        final Setting setting = Main.getSettings(guild);
        if (!GUILD.exists())
            GUILD.mkdirs();
        final File saveDir = new File(GUILD, guild.getId());
        if (!saveDir.exists())
            saveDir.mkdirs();
        final File jsonFile = new File(saveDir, guild.getId() + ".json");

        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
            saveGuild(guild);
        }
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFile));
        String line;
        String json = "";
        while ((line = bufferedReader.readLine()) != null) {
            json += line + "\n";
        }
        final JSONObject jsonObject = new JSONObject(json);
        final JSONObject settings = jsonObject.getJSONObject("settings");
        final JSONObject users = jsonObject.getJSONObject("user");

        if (setting != null) {
            for (String set : settings.keySet()) {
                setting.settings.put(set, settings.get(set));
            }

            if (users != null)
                for (String key : users.keySet()) {
                    setting.KOKS_USER.put(key, (String) users.get(key));
                }
        }

        final JSONArray blackList = jsonObject.getJSONArray("blacklist");
        assert setting != null;
        for (int i = 0; i < blackList.length(); i++) {
            final String word = blackList.getString(i);
            if (!setting.BLACKLISTED_WORDS.contains(word))
                setting.BLACKLISTED_WORDS.add(blackList.getString(i));
        }
    }

    public static void saveGuild(Guild guild) throws IOException {
        final Setting settings = Main.getSettings(guild);
        assert settings != null;
        if (!GUILD.exists())
            GUILD.mkdirs();
        final File saveDir = new File(GUILD, guild.getId());
        if (!saveDir.exists())
            saveDir.mkdirs();
        final File jsonFile = new File(saveDir, guild.getId() + ".json");
        if (!jsonFile.exists())
            jsonFile.createNewFile();

        final JSONObject json = new JSONObject();

        final JSONObject settingObject = new JSONObject();
        final JSONObject users = new JSONObject();

        for (String setting : settings.settings.keySet()) {
            settingObject.put(setting, settings.settings.get(setting));
        }
        for (String uuid : settings.KOKS_USER.keySet()) {
            users.put(uuid, settings.KOKS_USER.get(uuid));
        }

        json.put("settings", settingObject);
        json.put("user", users);
        json.put("blacklist", settings.BLACKLISTED_WORDS);

        final FileWriter fileWriter = new FileWriter(jsonFile);
        fileWriter.write(json.toString(2));
        fileWriter.close();

    }

}
