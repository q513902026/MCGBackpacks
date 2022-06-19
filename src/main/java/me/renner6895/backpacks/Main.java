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
    private NMSUtil nmsUtil;
    private final String pluginName;
    private Map<String, PluginPlayer> playerMap;
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
        for (final Backpack backpack : BackPackCache.backpackMap.values()) {
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
        if (!this.registerUtils()) {
            log.info("You must use the last release of the Minecraft version you are using.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (!me.hope.VersionCheck.checkCoreVersion(1, 0, 6)) {
            log.info("依赖的HopeCore版本不符合,关闭中");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.registerFiles();
        this.registerConfig();
        BackPackCache.register();
        this.registerEvents();
        registerCommands();
        long lastTime = System.currentTimeMillis();
        this.registerBackpacks();
        log.info("Register Backpacks Time-Consuming: " + (System.currentTimeMillis() - lastTime) + " ms");
        this.registerPlayers();

    }

    private boolean registerUtils() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException var3) {
            return false;
        }
        if ("v1_12_R1".equals(version)) {
            this.nmsUtil = new NMSUtil_1_12();
        }
        return this.nmsUtil != null;
    }

    private void registerPlayers() {
        log.info("Update PluginPlayers...");
        this.playerMap = new HashMap<>();
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
        Main.slotFiller = new SlotFiller(instance);
        Main.defaultName = instance.getConfig().getString("default-backpack.name");
        Main.defaultSlots = instance.getConfig().getInt("default-backpack.slots");
        Main.defaultItemId = instance.getConfig().getInt("default-backpack.item-id");
        Main.defaultItemData = (byte) instance.getConfig().getInt("default-backpack.item-data");
        BackPackCache.backpackMap = new HashMap<UUID, Backpack>();
        for (File file : new File(instance.getDataFolder() + File.separator + "backpacks").listFiles()) {
            Backpack bp = new Backpack(file, instance);
            BackPackCache.backpackMap.put(bp.getUniqueId(), bp);
        }
        log.info("Backpack Size: " + BackPackCache.backpackMap.size());
    }

    private void registerFiles() {
        final File backpacksFolder = new File(instance.getDataFolder() + File.separator + "backpacks");
        if (!backpacksFolder.exists()) {
            log.info("Generating backpacks folder...");
            backpacksFolder.mkdirs();
        }
    }
    private void registerConfig() {
        if (instance.getConfig().get("restore-defaults") == null || instance.getConfig().getBoolean("restore-defaults")) {
            instance.getConfig().set("restore-defaults", false);
            instance.getConfig().set("config-version", 1);
            instance.getConfig().set("prefix", "&3&l[&bMystical&7Backpacks&3&l]");
            instance.getConfig().set("default-backpack.item-id", 130);
            instance.getConfig().set("default-backpack.item-data", 0);
            instance.getConfig().set("default-backpack.name", "&5Mystical &8Backpack");
            instance.getConfig().set("default-backpack.slots", 27);
            instance.getConfig().set("slot-filler.item-id", 160);
            instance.getConfig().set("slot-filler.item-data", 15);
            instance.getConfig().set("slot-filler.name", "&cNo Access");
            this.registerLang();
        }
        instance.saveConfig();
    }

    private void registerLang() {
        instance.getConfig().set("give.error", "&cError: For information on how to use this command, type /backpacks help give");
        instance.getConfig().set("give.error2", "&7The player %s is not online");
        instance.getConfig().set("give.succuse", "&7New Backpack given to %s .");
        instance.getConfig().set("clone.error", "&cYou can only use this command as a player!");
        instance.getConfig().set("clone.succuse", "&7The backpack item has been cloned!");
        instance.getConfig().set("clone.error2", "&cYou must be holding a backpack for this to work!");
        instance.getConfig().set("rename.error", "&cError: You must be a player to use this command.");
        instance.getConfig().set("rename.error2", "&cError: For information on how to use this command, type /backpacks help rename");
        instance.getConfig().set("rename.error3", "&cError: You must be holding the backpack in your hand to rename it.");
        instance.getConfig().set("rename.succuse", "&7Backpack renamed to %s &7.");
        instance.getConfig().set("delete.error", "&cYou can only use this command as a player!");
        instance.getConfig().set("delete.succuse", "&7The backpack has been delete!");
        instance.getConfig().set("delete.error", "&cYou can only use this command as a player!");
    }

    private void registerEvents() {
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryEvents(instance), instance);
        pm.registerEvents(new JoinLeaveEvents(instance), instance);
        pm.registerEvents(new CraftingEvents(instance), instance);
    }

    /**
     * 注册命令
     */
    private void registerCommands() {
        adminCommand.registerCommand("create", injector.getSingleton(CreateCache.class));
        adminCommand.registerCommand("give", injector.getSingleton(GiveCommand.class));
        adminCommand.registerCommand("clone", injector.getSingleton(CloneCommand.class));
        adminCommand.registerCommand("rename", injector.getSingleton(RenameCommand.class));
        adminCommand.registerCommand("delete", injector.getSingleton(DeleteCommand.class));
        adminCommand.registerCommand("reslot", injector.getSingleton(ReslotCommand.class));
        adminCommand.registerCommand("find", injector.getSingleton(FindCommand.class));
        adminCommand.registerCommand("view", injector.getSingleton(ViewCommand.class));
        adminCommand.registerCommand("rebuildCache", injector.getSingleton(RebuildCacheCommand.class));
        adminCommand.registerCommand("viewall", injector.getSingleton(ViewAllCommand.class));
        adminCommand.registerCommand("help", injector.getSingleton(HelpCommand.class));
    }

    public boolean itemIsBackpack(final ItemStack item) {
        if (item != null && this.nmsUtil.getTag(item) != null && this.nmsUtil.hasKey(item, "backpack-item")) {
            final String backpackId = this.nmsUtil.getStringTag(item, "backpack-item");
            final File file = new File(instance.getDataFolder() + File.separator + "backpacks", backpackId + ".yml");
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
        Backpack bp = BackPackCache.backpackMap.get(uuid);
        if (!bp.isInit()) {
            bp.load();
        }
        return bp;
    }

    private File getBackpackFileByUUID(UUID uuid) {
        return new File(instance.getDataFolder() + File.separator + "backpacks", uuid + ".yml");
    }

    public void registerBackpack(final Backpack backpack) {
        BackPackCache.backpackMap.put(backpack.getUniqueId(), backpack);
    }

    public void unregisterBackpack(final Backpack backpack) {
        BackPackCache.backpackMap.remove(backpack.getUniqueId());
    }

    public PluginPlayer getPluginPlayer(final String id) {
        return this.playerMap.get(id);
    }

    private void linkPlayerToBackpack(final PluginPlayer pluginPlayer) {
        final String name = pluginPlayer.getPlayer().getName();
        for (final Map.Entry<UUID, Backpack> entry : BackPackCache.backpackMap.entrySet()) {
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
        final String prefix = instance.getConfig().getString("prefix");
        return (prefix != null && prefix.length() >= 1) ? (prefix + " ") : "";
    }

    public NMSUtil getNmsUtil() {
        return this.nmsUtil;
    }

    public Map<UUID, Backpack> getBackpackMap() {
        return BackPackCache.backpackMap;
    }

    public static Main INSTANCE() {
        return instance;
    }

    private void registerBeans() {
        injector = new InjectorBuilder().setPlugin(this).setDefaultPath("me.renner6895").build();

        log = injector.register(Logger.class, this.getLogger());
        adminCommand = injector.register(PluginCommandMap.class, new PluginCommandMap(this));
    }
}
