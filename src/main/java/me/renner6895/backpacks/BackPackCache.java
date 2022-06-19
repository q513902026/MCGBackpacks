package me.renner6895.backpacks;

import me.renner6895.backpacks.objects.Backpack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author xiaoyv_404
 */
public class BackPackCache {

    static Main plugin = Main.INSTANCE();

    static Logger log = plugin.getLogger();

    /**
     * 缓存存储位置
     **/
    private static File backpackCacheFile;

    /**
     * 背包缓存
     */
    static FileConfiguration cache;

    static Map<UUID, Backpack> backpackMap;

    /**
     * 重载缓存流
     */
    private static void reload() {
        final InputStream defConfigStream = plugin.getResource("cache.yml");
        if (defConfigStream == null) {
            return;
        }
        cache.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
    }

    /**
     * 必须在registerBackpack之前运行
     * 用于加载存储的背包拥有者信息
     */
    static void register() {
        backpackCacheFile = new File(plugin.getDataFolder(), "cache.yml");
        if (cache == null) {
            log.info("加载 背包缓存 ...");
            cache = YamlConfiguration.loadConfiguration(backpackCacheFile);
            reload();
        }
    }

    private void save() {
        try {
            cache.save(backpackCacheFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cacheBackpackInfo(String UUID, String bindID) {
        cacheBackpackInfo(UUID, bindID, false);
    }

    public void cacheBackpackInfo(String UUID, String bindID, boolean skipSave) {
        cache.set(UUID, bindID);
        if (skipSave) {
            return;
        }
        save();
    }

    public void build() {
        int index = 0;
        int maxLength = backpackMap.size();
        double perc;
        int phase;
        int lastPhase = 0;
        for (final Map.Entry<UUID, Backpack> entry : backpackMap.entrySet()) {
            perc = ((double) index / (double) maxLength) * 100;
            if ((phase = (int) (perc / 8)) > lastPhase) {
                lastPhase = phase;
                log.info(String.format("正在建立缓存,当前进度(%d/%d) - %d%% ...", index, maxLength, (int) perc));
            }
            entry.getValue().load();
            cacheBackpackInfo(entry.getKey().toString(), entry.getValue().getBindID(), true);
            index += 1;
        }
        save();
        log.info("缓存建立完成,数量:" + index);
    }

    public String getBackpackBindCache(String uuid) {
        return cache.getString(uuid);
    }

    public boolean hasBackpackCache(String UUID) {
        return cache.contains(UUID);
    }
}
