package bot.manager;

import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author kroko
 * @created on 19.01.2021 : 17:41
 */
public class Setting {

    public Guild guild;
    public HashMap<String, Object> settings = new HashMap<>();
    public final HashMap<String, String> KOKS_USER = new HashMap<>();
    public final ArrayList<String> BLACKLISTED_WORDS = new ArrayList<>();

    public final HashMap<String, String> INVITES = new HashMap<>();

    public Setting(Guild guild) {
        this.guild = guild;
        settings.put("allowNSWF", false);
        settings.put("deepAI-ApiKey", "");
    }

    public Object getValue(String setting) {
        return settings.get(setting);
    }
}
