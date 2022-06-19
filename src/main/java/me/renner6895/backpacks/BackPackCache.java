package me.renner6895.backpacks;

import com.google.common.collect.Maps;
import me.hope.core.inject.annotation.Inject;
import me.renner6895.backpacks.objects.Backpack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author xiaoyv_404
 */
public class BackPackCache {
    @Inject
    private static Main plugin;
    @Inject
    private static Logger log;

    /**
     * 缓存存储位置
     **/
    private  File backpackCacheFile;

    /**
     * 背包缓存
     */
    private FileConfiguration cache;

    private Map<UUID, Backpack> backpackMap;
    public BackPackCache(){
        backpackMap = Maps.newHashMap();

    }

    public void linkYamlFileToMap() {
        register();
        for (File file: new File(plugin.getDataFolder()+File.separator + "backpacks").listFiles(new YamlFileFilter())){
            Backpack bp = new Backpack(file);
            this.backpackMap.put(bp.getUniqueId(),bp);
        }
    }

    public Map<UUID,Backpack> getBackpackMap(){
        return backpackMap;
    }
    /**
     * 重载缓存流
     */
    private void reload() {
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
     public void register() {
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

    static class YamlFileFilter implements FilenameFilter{
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".yml");
        }
    }
}
