package me.renner6895.backpacks.tools;

import me.renner6895.backpacks.Main;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author xiaoyv_404
 */
public class FormatTool {

    static Main plugin = Main.INSTANCE();

    public static String getFormatText(final String key, final String defval) {
        final FileConfiguration config = plugin.getConfig();
        if (config.getConfigurationSection("lang." + key) == null) {
            config.set("lang." + key, defval);
        }
        plugin.saveConfig();
        return config.getString("lang." + key);
    }
}
