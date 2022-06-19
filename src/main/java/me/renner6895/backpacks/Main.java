package me.renner6895.backpacks;

import com.google.common.base.Charsets;
import me.hope.core.PluginCommandMap;
import me.hope.core.inject.Injector;
import me.hope.core.inject.InjectorBuilder;
import me.renner6895.backpacks.commands.*;
import me.renner6895.backpacks.events.CraftingEvents;
import me.renner6895.backpacks.events.InventoryEvents;
import me.renner6895.backpacks.events.JoinLeaveEvents;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.objects.PluginPlayer;
import me.renner6895.backpacks.objects.SlotFiller;
import me.renner6895.nmstag.NMSUtil;
import me.renner6895.nmstag.NMSUtil_1_12;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author HopeAsd
 * @author xiaoyv_404
 */
public class Main extends JavaPlugin {
    /**
     * 日志文件实例
     */
    public static Logger log;

    /**
     * 实例注入
     */
    private static Injector injector;

    /**
     * 插件实例
     */
    private static Main instance;

    /**
     * 管理命令的注册
     */
    private static PluginCommandMap<Main> adminCommand;

    private static Main plugin;
    private NMSUtil nmsUtil;
    private final String pluginName;
    private Map<UUID, Backpack> backpackMap;
    private Map<String, PluginPlayer> playerMap;
    private FileConfiguration backpackCache;
    private File backpackCacheFile;
    public static SlotFiller slotFiller;
    public static String defaultName;
    public static int defaultSlots;
    public static int defaultItemId;
    public static short defaultItemData;

    public Main() {
        pluginName = "Backpacks";
    }

    @Override
    public void onLoad() {
        registerBeans();
    }

    public void onDisable() {
        log.info("Save Backpacks......");
        long lastTime = System.currentTimeMillis();
        int length = 0;
        for (final Backpack backpack : this.backpackMap.values()) {
            if (backpack.isInit() && backpack.hasViewer()) {
                backpack.clearViewers();
                backpack.saveBackpack();
                length += 1;
            }
        }
        log.info("Saving Backpacks Size: " + length);
        log.info("Saving Backpacks Time-Consuming: " + (System.currentTimeMillis() - lastTime) + " ms");
        for (final PluginPlayer pluginPlayer : this.playerMap.values()) {
            pluginPlayer.removal();
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        if (!this.registerUtils()) {
            log.info("You must use the last release of the Minecraft version you are using.");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            this.registerFiles();
            this.registerConfig();
            this.registerCache();
            this.registerEvents();
            registerCommands();
            long lastTime = System.currentTimeMillis();
            this.registerBackpacks();
            log.info("Register Backpacks Time-Consuming: " + (System.currentTimeMillis() - lastTime) + " ms");
            this.registerPlayers();
        }
    }

    private boolean registerUtils() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException var3) {
            return false;
        }
        if (version.equals("v1_12_R1")) {
            this.nmsUtil = new NMSUtil_1_12();
        }
        return this.nmsUtil != null;
    }

    private void registerPlayers() {
        log.info("Update PluginPlayers...");
        this.playerMap = new HashMap<String, PluginPlayer>();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            try {
                this.registerPlayer(new PluginPlayer(player));
            } catch (NullPointerException e) {
                log.warning("注册玩家; " + player.getName() + "时出错");
            }
        }
    }

    private void registerBackpacks() {
        log.info("Update Backpacks...");
        Main.slotFiller = new SlotFiller(plugin);
        Main.defaultName = plugin.getConfig().getString("default-backpack.name");
        Main.defaultSlots = plugin.getConfig().getInt("default-backpack.slots");
        Main.defaultItemId = plugin.getConfig().getInt("default-backpack.item-id");
        Main.defaultItemData = (byte) plugin.getConfig().getInt("default-backpack.item-data");
        this.backpackMap = new HashMap<UUID, Backpack>();
        for (File file : new File(plugin.getDataFolder() + File.separator + "backpacks").listFiles()) {
            Backpack bp = new Backpack(file, plugin);
            this.backpackMap.put(bp.getUniqueId(), bp);
        }
        log.info("Backpack Size: " + this.backpackMap.size());
    }

    private void registerFiles() {
        final File backpacksFolder = new File(plugin.getDataFolder() + File.separator + "backpacks");
        if (!backpacksFolder.exists()) {
            log.info("Generating backpacks folder...");
            backpacksFolder.mkdirs();
        }
    }


    private void reloadCache() {
        final InputStream defConfigStream = this.getResource("cache.yml");
        if (defConfigStream == null) {
            return;
        }
        backpackCache.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    /**
     * 必须在registerBackpack之前运行
     * 用于加载存储的背包拥有者信息
     */
    private void registerCache() {
        backpackCacheFile = new File(this.getDataFolder(), "cache.yml");
        if (backpackCache == null) {
            log.info("加载 背包缓存 ...");
            backpackCache = YamlConfiguration.loadConfiguration(backpackCacheFile);
            reloadCache();
        }
    }

    public void cacheBackpackInfo(String UUID, String bindID) {
        cacheBackpackInfo(UUID, bindID, false);
    }

    public void cacheBackpackInfo(String UUID, String bindID, boolean skipSave) {
        backpackCache.set(UUID, bindID);
        if (skipSave) {
            return;
        }
        saveBackpackCache();
    }

    private void saveBackpackCache() {
        try {
            backpackCache.save(backpackCacheFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBackpackBindCache(String UUID) {
        return backpackCache.getString(UUID);
    }

    public boolean hasBackpackCache(String UUID) {
        return backpackCache.contains(UUID);
    }

    public void buildCache() {
        int index = 0;
        int maxLength = this.backpackMap.size();
        double perc;
        int phase;
        int lastPhase = 0;
        for (final Map.Entry<UUID, Backpack> entry : this.backpackMap.entrySet()) {
            perc = ((double) index / (double) maxLength) * 100;
            if ((phase = (int) (perc / 8)) > lastPhase) {
                lastPhase = phase;
                log.info(String.format("正在建立缓存,当前进度(%d/%d) - %d%% ...", index, maxLength, (int) perc));
            }
            entry.getValue().load();
            cacheBackpackInfo(entry.getKey().toString(), entry.getValue().getBindID(), true);
            index += 1;

        }
        saveBackpackCache();
        log.info("缓存建立完成,数量:" + index);
    }

    private void registerConfig() {
        if (plugin.getConfig().get("restore-defaults") == null || plugin.getConfig().getBoolean("restore-defaults")) {
            plugin.getConfig().set("restore-defaults", false);
            plugin.getConfig().set("config-version", 1);
            plugin.getConfig().set("prefix", "&3&l[&bMystical&7Backpacks&3&l]");
            plugin.getConfig().set("default-backpack.item-id", 130);
            plugin.getConfig().set("default-backpack.item-data", 0);
            plugin.getConfig().set("default-backpack.name", "&5Mystical &8Backpack");
            plugin.getConfig().set("default-backpack.slots", 27);
            plugin.getConfig().set("slot-filler.item-id", 160);
            plugin.getConfig().set("slot-filler.item-data", 15);
            plugin.getConfig().set("slot-filler.name", "&cNo Access");
            this.registerLang();
        }
        plugin.saveConfig();
    }

    private void registerLang() {
        plugin.getConfig().set("give.error", "&cError: For information on how to use this command, type /backpacks help give");
        plugin.getConfig().set("give.error2", "&7The player %s is not online");
        plugin.getConfig().set("give.succuse", "&7New Backpack given to %s .");
        plugin.getConfig().set("clone.error", "&cYou can only use this command as a player!");
        plugin.getConfig().set("clone.succuse", "&7The backpack item has been cloned!");
        plugin.getConfig().set("clone.error2", "&cYou must be holding a backpack for this to work!");
        plugin.getConfig().set("rename.error", "&cError: You must be a player to use this command.");
        plugin.getConfig().set("rename.error2", "&cError: For information on how to use this command, type /backpacks help rename");
        plugin.getConfig().set("rename.error3", "&cError: You must be holding the backpack in your hand to rename it.");
        plugin.getConfig().set("rename.succuse", "&7Backpack renamed to %s &7.");
        plugin.getConfig().set("delete.error", "&cYou can only use this command as a player!");
        plugin.getConfig().set("delete.succuse", "&7The backpack has been delete!");
        plugin.getConfig().set("delete.error", "&cYou can only use this command as a player!");
    }

    private void registerEvents() {
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryEvents(plugin), plugin);
        pm.registerEvents(new JoinLeaveEvents(plugin), plugin);
        pm.registerEvents(new CraftingEvents(plugin), plugin);
    }

    /**
     * 注册命令
     */
    private void registerCommands() {
        adminCommand.registerCommand("backpack", new BackpackCMD(plugin));
        adminCommand.registerCommand("create", injector.getSingleton(CreateCache.class));
        adminCommand.registerCommand("give", injector.getSingleton(GiveCommand.class));
        adminCommand.registerCommand("clone", injector.getSingleton(CloneCommand.class));
        adminCommand.registerCommand("rename", injector.getSingleton(RenameCommand.class));
        adminCommand.registerCommand("delete", injector.getSingleton(DeleteCommand.class));
        adminCommand.registerCommand("reslot", injector.getSingleton(ReslotCommand.class));
        adminCommand.registerCommand("find", injector.getSingleton(FindCommand.class));
        adminCommand.registerCommand("view", injector.getSingleton(ViewCommand.class));
        adminCommand.registerCommand("rebuildCache", injector.getSingleton(RebuildCacheCommand.class));
        adminCommand.registerCommand("viewall",injector.getSingleton(ViewAllCommand.class));
    }

    public boolean itemIsBackpack(final ItemStack item) {
        if (item != null && this.nmsUtil.getTag(item) != null && this.nmsUtil.hasKey(item, "backpack-item")) {
            final String backpackId = this.nmsUtil.getStringTag(item, "backpack-item");
            final File file = new File(plugin.getDataFolder() + File.separator + "backpacks", backpackId + ".yml");
            if (file.exists()) {
                return true;
            }
            if (backpackId != null) {
                item.setAmount(0);
            }
        }
        return false;
    }

    public Backpack getBackpack(final UUID uuid) {
        Backpack bp = this.backpackMap.get(uuid);
        if (!bp.isInit()) {
            bp.load();
        }
        return bp;
    }

    private File getBackpackFileByUUID(UUID uuid) {
        return new File(plugin.getDataFolder() + File.separator + "backpacks", uuid + ".yml");
    }

    public void registerBackpack(final Backpack backpack) {
        this.backpackMap.put(backpack.getUniqueId(), backpack);
    }

    public void unregisterBackpack(final Backpack backpack) {
        this.backpackMap.remove(backpack.getUniqueId());
    }

    public PluginPlayer getPluginPlayer(final String id) {
        return this.playerMap.get(id);
    }

    private void linkPlayerToBackpack(final PluginPlayer pluginPlayer) {
        final String name = pluginPlayer.getPlayer().getName();
        for (final Map.Entry<UUID, Backpack> entry : this.backpackMap.entrySet()) {
            if (entry.getValue().getBackpackForName(name) != null) {
                pluginPlayer.addBackpack(entry.getValue());
            }
        }
    }

    public void registerPlayer(final PluginPlayer pluginPlayer) {
        this.linkPlayerToBackpack(pluginPlayer);
        this.playerMap.put(pluginPlayer.getPlayer().getName(), pluginPlayer);
    }

    public void unregisterPlayer(final PluginPlayer pluginPlayer) {
        this.playerMap.remove(pluginPlayer.getPlayer().getName());
    }

    public String getPrefix() {
        final String prefix = plugin.getConfig().getString("prefix");
        return (prefix != null && prefix.length() >= 1) ? (prefix + " ") : "";
    }

    public NMSUtil getNmsUtil() {
        return this.nmsUtil;
    }

    public Map<UUID, Backpack> getBackpackMap() {
        return this.backpackMap;
    }

    public static Main INSTANCE() {
        return plugin;
    }

    private void registerBeans() {
        injector = new InjectorBuilder().setPlugin(this).setDefaultPath("me.renner6895").build();

        log = injector.register(Logger.class, this.getLogger());
        adminCommand = injector.register(PluginCommandMap.class, new PluginCommandMap(this));
    }
}
