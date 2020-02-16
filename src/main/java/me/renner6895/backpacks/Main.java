package me.renner6895.backpacks;

import org.bukkit.plugin.java.*;
import me.renner6895.backpacks.objects.*;
import org.bukkit.*;
import nmstag.*;

import java.util.*;

import org.bukkit.entity.*;

import java.io.*;

import org.bukkit.event.*;
import me.renner6895.backpacks.events.*;
import org.bukkit.plugin.*;
import me.renner6895.backpacks.commands.*;
import org.bukkit.command.*;
import org.bukkit.inventory.*;

public class Main extends JavaPlugin {
    private Main plugin;
    private NMSUtil nmsUtil;
    private String pluginName;
    private Map<UUID, Backpack> backpackMap;
    private Map<String, PluginPlayer> playerMap;
    public static SlotFiller slotFiller;
    public static String defaultName;
    public static int defaultSlots;
    public static int defaultItemId;
    public static short defaultItemData;

    public Main() {
        this.pluginName = "Backpacks";
    }

    public void onDisable() {
        for (final Backpack backpack : this.backpackMap.values()) {
            backpack.clearViewers();
            backpack.saveBackpack();
        }
        for (final PluginPlayer pluginPlayer : this.playerMap.values()) {
            pluginPlayer.removal();
        }
    }

    public void onEnable() {
        this.plugin = this;
        if (!this.registerUtils()) {
            this.log("You must use the last release of the Minecraft version you are using.");
            Bukkit.getPluginManager().disablePlugin((Plugin) this);
        } else {
            this.registerFiles();
            this.registerConfig();
            this.registerEvents();
            this.registerCommands();
            this.registerBackpacks();
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
        this.playerMap = new HashMap<String, PluginPlayer>();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.registerPlayer(new PluginPlayer(player));
        }
    }

    private void registerBackpacks() {
        Main.slotFiller = new SlotFiller(this.plugin);
        Main.defaultName = this.plugin.getConfig().getString("default-backpack.name");
        Main.defaultSlots = this.plugin.getConfig().getInt("default-backpack.slots");
        Main.defaultItemId = this.plugin.getConfig().getInt("default-backpack.item-id");
        Main.defaultItemData = (byte) this.plugin.getConfig().getInt("default-backpack.item-data");
        this.backpackMap = new HashMap<UUID, Backpack>();
        File[] var4;
        for (int var3 = (var4 = new File(this.plugin.getDataFolder() + File.separator + "backpacks").listFiles()).length, var5 = 0; var5 < var3; ++var5) {
            final File file = var4[var5];
            final Backpack b = new Backpack(file, this.plugin);
            this.backpackMap.put(b.getUniqueId(), b);
        }
    }

    private void registerFiles() {
        final File backpacksFolder = new File(this.plugin.getDataFolder() + File.separator + "backpacks");
        if (!backpacksFolder.exists()) {
            this.log("Generating backpacks folder...");
            backpacksFolder.mkdirs();
        }
    }

    private void registerConfig() {
        if (this.plugin.getConfig().get("restore-defaults") == null || this.plugin.getConfig().getBoolean("restore-defaults")) {
            this.plugin.getConfig().set("restore-defaults", (Object) false);
            this.plugin.getConfig().set("config-version", (Object) 1);
            this.plugin.getConfig().set("prefix", (Object) "&3&l[&bMystical&7Backpacks&3&l]");
            this.plugin.getConfig().set("default-backpack.item-id", (Object) 130);
            this.plugin.getConfig().set("default-backpack.item-data", (Object) 0);
            this.plugin.getConfig().set("default-backpack.name", (Object) "&5Mystical &8Backpack");
            this.plugin.getConfig().set("default-backpack.slots", (Object) 27);
            this.plugin.getConfig().set("slot-filler.item-id", (Object) 160);
            this.plugin.getConfig().set("slot-filler.item-data", (Object) 15);
            this.plugin.getConfig().set("slot-filler.name", (Object) "&cNo Access");
            this.registerLang();
        }
        this.plugin.saveConfig();
    }

    private void registerLang() {
        this.plugin.getConfig().set("give.error", (Object) "&cError: For information on how to use this command, type /backpacks help give");
        this.plugin.getConfig().set("give.error2", (Object) "&7The player %s is not online");
        this.plugin.getConfig().set("give.succuse", (Object) "&7New Backpack given to %s .");
        this.plugin.getConfig().set("clone.error", (Object) "&cYou can only use this command as a player!");
        this.plugin.getConfig().set("clone.succuse", (Object) "&7The backpack item has been cloned!");
        this.plugin.getConfig().set("clone.error2", (Object) "&cYou must be holding a backpack for this to work!");
        this.plugin.getConfig().set("rename.error", (Object) "&cError: You must be a player to use this command.");
        this.plugin.getConfig().set("rename.error2", (Object) "&cError: For information on how to use this command, type /backpacks help rename");
        this.plugin.getConfig().set("rename.error3", (Object) "&cError: You must be holding the backpack in your hand to rename it.");
        this.plugin.getConfig().set("rename.succuse", (Object) "&7Backpack renamed to %s &7.");
        this.plugin.getConfig().set("delete.error", (Object) "&cYou can only use this command as a player!");
        this.plugin.getConfig().set("delete.succuse", (Object) "&7The backpack has been delete!");
        this.plugin.getConfig().set("delete.error", (Object) "&cYou can only use this command as a player!");
    }

    private void registerEvents() {
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents((Listener) new InventoryEvents(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new JoinLeaveEvents(this.plugin), (Plugin) this.plugin);
        pm.registerEvents((Listener) new CraftingEvents(this.plugin), (Plugin) this.plugin);
    }

    private void registerCommands() {
        this.plugin.getCommand("backpack").setExecutor((CommandExecutor) new BackpackCMD(this.plugin));
        this.plugin.getCommand("backpacks").setExecutor((CommandExecutor) new BackpackCMD(this.plugin));
        this.plugin.getCommand("bp").setExecutor((CommandExecutor) new BackpackCMD(this.plugin));
    }

    public boolean itemIsBackpack(final ItemStack item) {
        if (item != null && this.nmsUtil.getTag(item) != null && this.nmsUtil.hasKey(item, "backpack-item")) {
            final String backpackId = this.nmsUtil.getStringTag(item, "backpack-item");
            final File file = new File(this.plugin.getDataFolder() + File.separator + "backpacks", backpackId + ".yml");
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
        return this.backpackMap.get(uuid);
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
        final String prefix = this.plugin.getConfig().getString("prefix");
        return (prefix != null && prefix.length() >= 1) ? (prefix + " ") : "";
    }

    public NMSUtil getNmsUtil() {
        return this.nmsUtil;
    }

    public void log(final Object o) {
        System.out.println("[" + this.pluginName + "] " + o);
    }

    public void debug(final Object o) {
        Bukkit.broadcastMessage("[" + this.pluginName + "] " + o);
        this.log(o);
    }

    public Map<UUID, Backpack> getBackpackMap() {
        return this.backpackMap;
    }
}
