package me.renner6895.backpacks.objects;

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
    private final Main plugin;
    private final UUID Backpack_ID;
    private final File file;
    private String User_ID;
    private int slots;
    private String name;
    private int itemId;
    private short itemData;
    private Inventory inventory;

    public Backpack(final File file, final Main plugin) {
        this.file = file;
        this.plugin = plugin;
        this.Backpack_ID = UUID.fromString(this.file.getName().substring(0, this.file.getName().length() - 4));
        this.User_ID = Main.INSTANCE().hasBackpackCache(this.Backpack_ID.toString()) ? Main.INSTANCE().getBackpackBindCache(this.Backpack_ID.toString()) : null;
    }

    private void initialize() {
        try {
            final FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(this.file);
            this.name = ((fileConfig.get("name") == null) ? Main.defaultName : fileConfig.getString("name"));
            this.slots = ((fileConfig.get("slots") == null) ? Main.defaultSlots : fileConfig.getInt("slots"));
            this.itemId = ((fileConfig.get("item-id") == null) ? Main.defaultItemId : fileConfig.getInt("item-id"));
            this.itemData = ((fileConfig.get("item-data") == null) ? Main.defaultItemData : ((short) fileConfig.getInt("item-data")));
            this.User_ID = fileConfig.getString("bind-id");
        } catch (NullPointerException e) {
            Main.INSTANCE().log(this.file.getName() + "载入时发生了错误,可能是错误的保存物品导致的");
        }
    }

    private void loadInv(ConfigurationSection fileConfig) {
        this.initialize();
        this.inventory = Bukkit.createInventory(new BackpackHolder(this.plugin, this.Backpack_ID), 54, ChatColor.translateAlternateColorCodes('&', this.getName()));
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
        if (inventory == null) {
            loadInv(YamlConfiguration.loadConfiguration(this.file));
        }
    }

    public boolean isInit() {
        return inventory != null;
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
    public boolean hasViewer(){
        if(isInit()){
            return !this.getInventory().getViewers().isEmpty();
        }
        return false;
    }
    public void updateBackpack() {
        this.saveBackpack();
        this.clearViewers();
        this.initialize();
    }

    public Inventory getPackInventory() {
        return this.inventory;
    }

    public void saveBackpack() {

        if (isInit()) {
            final FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(this.file);
            fileConfig.set("name", this.name);
            fileConfig.set("slots", this.getSlots());
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
        this.plugin.unregisterBackpack(this);
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
        lore.add("Bind-Player: " + Bukkit.getOfflinePlayer(this.getBindID()).getName());
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Slots: &c" + this.getSlots()));
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        item = this.plugin.getNmsUtil().setStringTag(item, "backpack-item", this.getUniqueId().toString());
        item = this.plugin.getNmsUtil().setStringTag(item, "backpack-owner", this.getBindID());
        return item;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', this.name);
    }

    public void updateName(final String name) {


        this.clearViewers();
        final FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(this.file);
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
        if (this.User_ID == null) {
            final FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(this.file);
            this.User_ID = fileConfig.getString("bind-id");
            Main.INSTANCE().cacheBackpackInfo(this.Backpack_ID.toString(), this.User_ID);
        }
        if (name.equals(this.getBindID())) {
            return this;
        }
        return null;
    }

    public int getSlots() {
        return this.slots;
    }

    public void updateSlots(final int slots) {
        this.clearViewers();
        final FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(this.file);
        fileConfig.set("name", this.name);
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
        return this.User_ID;
    }
}
