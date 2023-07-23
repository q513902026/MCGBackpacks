package me.renner6895.backpacks;

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
import me.renner6895.backpacks.tools.ColorTool;
import me.renner6895.nmstag.NMSUtil;
import me.renner6895.nmstag.NMSUtil_1_12;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
     *
     */
    private BackPackCache backPackCache;
    /**
     * 管理命令的注册
     */
    private static PluginCommandMap<Main> adminCommand;

    private NMSUtil nmsUtil;
    private Map<String, PluginPlayer> playerMap;
    public static SlotFiller slotFiller;
    public static String defaultName;
    public static int defaultSlots;
    public static int defaultItemId;
    public static short defaultItemData;

    public Main() {
    }

    @Override
    public void onLoad() {
        registerBeans();
    }

    public void onDisable() {
        log.info("Save Backpacks......");
        long lastTime = System.currentTimeMillis();
        int length = 0;
        for (final Backpack backpack : backPackCache.getBackpackMap().values()) {
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
        injector.injectClasses();
        this.registerFiles();
        this.registerConfig();
        this.registerEvents();
        this.registerCommands();
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
        Main.slotFiller = new SlotFiller();
        Main.slotFiller.refresh();
        Main.defaultName = this.getConfig().getString("default-backpack.name");
        Main.defaultSlots = this.getConfig().getInt("default-backpack.slots");
        Main.defaultItemId = this.getConfig().getInt("default-backpack.item-id");
        Main.defaultItemData = (byte) this.getConfig().getInt("default-backpack.item-data");

        backPackCache.linkYamlFileToMap();
        log.info("Backpack Size: " + backPackCache.getBackpackMap().size());
    }

    private void registerFiles() {
        final File backpacksFolder = new File(this.getDataFolder() + File.separator + "backpacks");
        if (!backpacksFolder.exists()) {
            log.info("Generating backpacks folder...");
            backpacksFolder.mkdirs();
        }
    }

    private void registerConfig() {
        if (this.getConfig().get("restore-defaults") == null || this.getConfig().getBoolean("restore-defaults")) {
            this.getConfig().set("restore-defaults", false);
            this.getConfig().set("config-version", 1);
            this.getConfig().set("prefix", "&3&l[&bMystical&7Backpacks&3&l]");
            this.getConfig().set("default-backpack.item-id", 130);
            this.getConfig().set("default-backpack.item-data", 0);
            this.getConfig().set("default-backpack.name", "&5Mystical &8Backpack");
            this.getConfig().set("default-backpack.slots", 27);
            this.getConfig().set("slot-filler.item-id", 160);
            this.getConfig().set("slot-filler.item-data", 15);
            this.getConfig().set("slot-filler.name", "&cNo Access");
            this.registerLang();
        }
        this.saveConfig();
    }

    private void registerLang() {
        this.getConfig().set("give.error", "&cError: For information on how to use this command, type /backpacks help give");
        this.getConfig().set("give.error2", "&7The player %s is not online");
        this.getConfig().set("give.succuse", "&7New Backpack given to %s .");
        this.getConfig().set("clone.error", "&cYou can only use this command as a player!");
        this.getConfig().set("clone.succuse", "&7The backpack item has been cloned!");
        this.getConfig().set("clone.error2", "&cYou must be holding a backpack for this to work!");
        this.getConfig().set("rename.error", "&cError: You must be a player to use this command.");
        this.getConfig().set("rename.error2", "&cError: For information on how to use this command, type /backpacks help rename");
        this.getConfig().set("rename.error3", "&cError: You must be holding the backpack in your hand to rename it.");
        this.getConfig().set("rename.succuse", "&7Backpack renamed to %s &7.");
        this.getConfig().set("delete.error", "&cYou can only use this command as a player!");
        this.getConfig().set("delete.succuse", "&7The backpack has been delete!");
        this.getConfig().set("delete.error", "&cYou can only use this command as a player!");
    }

    private void registerEvents() {
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new InventoryEvents(), this);
        pm.registerEvents(new JoinLeaveEvents(), this);
        pm.registerEvents(new CraftingEvents(), this);
    }

    /**
     * 注册命令
     */
    private void registerCommands() {
        adminCommand.registerCommand("create", injector.getSingleton(CreateCommand.class));
        adminCommand.registerCommand("give", injector.getSingleton(GiveCommand.class));
        adminCommand.registerCommand("clone", injector.getSingleton(CloneCommand.class));
        adminCommand.registerCommand("rename", injector.getSingleton(RenameCommand.class));
        adminCommand.registerCommand("delete", injector.getSingleton(DeleteCommand.class));
        adminCommand.registerCommand("reslot", injector.getSingleton(ReslotCommand.class));
        adminCommand.registerCommand("find", injector.getSingleton(FindCommand.class));
        adminCommand.registerCommand("view", injector.getSingleton(ViewCommand.class));
        adminCommand.registerCommand("rebind", injector.getSingleton(ReBindCommand.class));
        adminCommand.registerCommand("rebuildCache", injector.getSingleton(RebuildCacheCommand.class));
        adminCommand.registerCommand("viewall", injector.getSingleton(ViewAllCommand.class));
        adminCommand.registerCommand("help", injector.getSingleton(HelpCommand.class));
        adminCommand.registerCommand("info", injector.getSingleton(InfoCommand.class));

        this.getCommand("backpacks").setExecutor(adminCommand);
    }

    public boolean itemIsBackpack(final ItemStack item) {
        if (item != null && this.nmsUtil.getTag(item) != null && this.nmsUtil.hasKey(item, "backpack-item")) {
            final String backpackId = this.nmsUtil.getStringTag(item, "backpack-item");
            final File file = new File(this.getDataFolder() + File.separator + "backpacks", backpackId + ".yml");
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
        Backpack bp = backPackCache.getBackpackMap().get(uuid);
        if (!bp.isInit()) {
            bp.load();
        }
        return bp;
    }

    private File getBackpackFileByUUID(UUID uuid) {
        return new File(this.getDataFolder() + File.separator + "backpacks", uuid + ".yml");
    }

    public void registerBackpack(final Backpack backpack) {
        backPackCache.getBackpackMap().put(backpack.getUniqueId(), backpack);
    }

    public void unregisterBackpack(final Backpack backpack) {
        backPackCache.getBackpackMap().remove(backpack.getUniqueId());
    }

    public PluginPlayer getPluginPlayer(final String id) {
        return this.playerMap.get(id);
    }

    private void linkPlayerToBackpack(final PluginPlayer pluginPlayer) {
        final String name = pluginPlayer.getPlayer().getName();
        for (final Map.Entry<UUID, Backpack> entry : backPackCache.getBackpackMap().entrySet()) {
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
        final String prefix = this.getConfig().getString("prefix");
        return (prefix != null && prefix.length() >= 1) ? (prefix + " ") : "";
    }

    public NMSUtil getNmsUtil() {
        return this.nmsUtil;
    }

    private void registerBeans() {
        injector = new InjectorBuilder().setPlugin(this).setDefaultPath("me.renner6895.backpacks").build();

        injector.register(Main.class, this);
        log = injector.register(Logger.class, this.getLogger());

        adminCommand = injector.register(PluginCommandMap.class, new PluginCommandMap<>(this));

        backPackCache = injector.register(BackPackCache.class, new BackPackCache());
    }

    public Optional<Backpack> getBackpackByItem(ItemStack stack) {
        if (this.itemIsBackpack(stack)) {
            final String backpackId = this.nmsUtil.getStringTag(stack, "backpack-item");
            return Optional.of(getBackpack(UUID.fromString(backpackId)));
        }
        return Optional.empty();
    }

    public Optional<Backpack> getBackpackByUUIDString(String UUIDString) {
        try {
            return Optional.of(getBackpack(UUID.fromString(UUIDString)));
        } catch (IllegalArgumentException exception) {
            log.warning("错误的UUID格式");
        }
        return Optional.empty();
    }
}
