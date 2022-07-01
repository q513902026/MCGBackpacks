package me.renner6895.backpacks.objects;

import me.hope.core.inject.annotation.Inject;
import me.renner6895.backpacks.BackPackCache;
import me.renner6895.backpacks.InvUtil;
import me.renner6895.backpacks.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Backpack {
    @Inject
    private static Main plugin;
    @Inject
    private static BackPackCache backPackCache;

    private final UUID Backpack_ID;
    private final File file;
    private String User_ID;
    private int slots;
    private String name;
    private int itemId;
    private short itemData;
    private Inventory inventory;
    private FileConfiguration fileConfig;
    private boolean isAdminBackpack;
    private boolean load = false;

    public Backpack(final File file) {
        this.file = file;
        this.Backpack_ID = UUID.fromString(this.file.getName().substring(0, this.file.getName().length() - 4));
        this.User_ID = backPackCache.hasBackpackCache(this.Backpack_ID.toString()) ? backPackCache.getBackpackBindCache(this.Backpack_ID.toString()) : null;
        this.isAdminBackpack = false;
    }

    private void initialize() {
        try {
            fileConfig = getFileConfig();
            this.name = ((fileConfig.get("name") == null) ? Main.defaultName : fileConfig.getString("name"));
            this.slots = ((fileConfig.get("slots") == null) ? Main.defaultSlots : fileConfig.getInt("slots"));
            this.itemId = ((fileConfig.get("item-id") == null) ? Main.defaultItemId : fileConfig.getInt("item-id"));
            this.itemData = ((fileConfig.get("item-data") == null) ? Main.defaultItemData : ((short) fileConfig.getInt("item-data")));
            this.User_ID = fileConfig.getString("bind-id");
            this.isAdminBackpack = (this.User_ID == null);
        } catch (NullPointerException e) {
            Main.log.warning(this.file.getName() + "载入时发生了错误,可能是错误的保存物品导致的");
        }
    }

    public void rebind(String bindName) {
        load();
        if (this.isAdminBackpack) {
            Main.log.warning("背包<" + this.Backpack_ID.toString() + ">正在从管理员背包重新绑定为个人背包,绑定对象:" + bindName);
        }
        this.User_ID = bindName;
        if (backPackCache.hasBackpackCache(this.Backpack_ID.toString())) {
            backPackCache.cacheBackpackInfo(this.Backpack_ID.toString(), bindName);
        }
        updateBackpack();
    }

    private void loadInv(ConfigurationSection fileConfig) {
        this.inventory = Bukkit.createInventory(new BackpackHolder(this.Backpack_ID), 54, getRawName());
        try {
            this.inventory.setContents(InvUtil.loadInventory(fileConfig));
        } catch (InvalidConfigurationException | IllegalArgumentException ex4) {

            ex4.printStackTrace();
        }
        for (int i = this.slots; i < this.inventory.getSize(); ++i) {
            this.inventory.setItem(i, Main.slotFiller.getItem());
        }
    }

    public void load() {
        if (!load) {
            initialize();
            loadInv(getFileConfig());
            load = true;
        }
    }

    public boolean isInit() {
        return load;
    }

    public void clearViewers() {
        if (this.getInventory() != null) {
            final Iterator<HumanEntity> hei = this.getInventory().getViewers().iterator();
            while (hei.hasNext()) {
                final HumanEntity he = hei.next();
                hei.remove();
                he.closeInventory();
            }
        }
    }

    public boolean hasViewer() {
        if (isInit()) {
            return !this.getInventory().getViewers().isEmpty();
        }
        return false;
    }

    public void updateBackpack() {
        this.saveBackpack();
        this.clearViewers();
        this.load = false;
        this.load();
    }

    public static boolean isOwner(Backpack backpack, String name) {
        if (backpack.getBindID() == null) {
            return false;
        }
        return backpack.getBindID().equals(name);
    }

    public Inventory getPackInventory() {
        return this.inventory;
    }

    public void saveBackpack() {

        if (isInit()) {
            final FileConfiguration fileConfig = getFileConfig();
            fileConfig.set("name", this.name);
            fileConfig.set("slots", this.getSlots());
            fileConfig.set("bind-id", this.getBindID());
            InvUtil.saveInventory(this.inventory, fileConfig, this.getSlots());
            try {
                fileConfig.save(this.file);
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }
    }

    public void removeBackpack() {
        this.clearViewers();
        plugin.unregisterBackpack(this);
        this.file.delete();
    }

    public ItemStack getItem() {
        if (!isInit()) {
            load();
        }
        //noinspection deprecation
        ItemStack item = new ItemStack(this.getItemId(), 1, this.getItemData());

        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.getName());
        final List<String> lore = new ArrayList<String>();
        lore.add("Bind-Player: " + this.getBindID());
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Slots: &c" + this.getSlots()));
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        item = plugin.getNmsUtil().setStringTag(item, "backpack-item", this.getUniqueId().toString());

        if (this.getBindID() == null) {
            return item;
        }

        item = plugin.getNmsUtil().setStringTag(item, "backpack-owner", this.getBindID());
        return item;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', this.getRawName());
    }

    public void updateName(final String name) {
        this.clearViewers();
        final FileConfiguration fileConfig = getFileConfig();
        fileConfig.set("name", name);
        fileConfig.set("slots", this.getSlots());
        InvUtil.saveInventory(this.inventory, fileConfig, this.getSlots());
        try {
            fileConfig.save(this.file);
        } catch (IOException var4) {
            var4.printStackTrace();
        }
        this.initialize();
        this.name = name;
    }

    public Backpack getBackpackForName(final String name) {

        if (name.equals(this.getBindID())) {
            return this;
        }
        return null;
    }

    public int getSlots() {
        if (this.slots == 0) {
            final FileConfiguration fileConfig = getFileConfig();
            this.slots = ((fileConfig.get("slots") == null) ? Main.defaultSlots : fileConfig.getInt("slots"));
        }
        return this.slots;
    }

    public void updateSlots(final int slots) {
        this.clearViewers();
        final FileConfiguration fileConfig = getFileConfig();
        fileConfig.set("name", getRawName());
        fileConfig.set("slots", slots);
        InvUtil.saveInventory(this.inventory, fileConfig, this.getSlots());
        try {
            fileConfig.save(this.file);
        } catch (IOException var4) {
            var4.printStackTrace();
        }
        this.initialize();
        this.slots = slots;
    }

    private String getRawName() {
        if (!isInit()) {
            final FileConfiguration fileConfig = getFileConfig();
            this.name = ((fileConfig.get("name") == null) ? Main.defaultName : fileConfig.getString("name"));
        }
        return this.name;
    }

    public short getItemData() {
        return this.itemData;
    }

    public int getItemId() {
        return this.itemId;
    }

    public UUID getUniqueId() {
        return this.Backpack_ID;
    }

    public String getBindID() {
        if (backPackCache.hasBackpackCache(this.Backpack_ID.toString())) {
            return backPackCache.getBackpackBindCache(this.Backpack_ID.toString());
        }
        if (this.User_ID == null && !this.isAdminBackpack) {
            final FileConfiguration fileConfig = getFileConfig();
            this.User_ID = fileConfig.getString("bind-id");
            if (this.User_ID == null) {
                this.isAdminBackpack = true;
                return null;
            }
            backPackCache.cacheBackpackInfo(this.Backpack_ID.toString(), this.User_ID);
        }
        return this.User_ID;
    }

    public FileConfiguration getFileConfig() {
        return getFileConfig(false);
    }

    public FileConfiguration getFileConfig(boolean reload) {
        if (reload) {
            fileConfig = YamlConfiguration.loadConfiguration(this.file);
        } else {
            if (fileConfig == null) {
                fileConfig = getFileConfig(true);
            }
        }
        return fileConfig;
    }
}
