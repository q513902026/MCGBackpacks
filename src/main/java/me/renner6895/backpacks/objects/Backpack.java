package me.renner6895.backpacks.objects;

import org.bukkit.*;
import me.renner6895.backpacks.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;

import java.io.*;

import org.bukkit.inventory.*;

import java.util.*;

import org.bukkit.inventory.meta.*;

public class Backpack {
    private Main plugin;
    private UUID Backpack_ID;
    private String User_ID;
    private int slots;
    private String name;
    private int itemId;
    private short itemData;
    private Inventory inventory;
    private File file;

    public Backpack(final File file, final Main plugin) {
        this.file = file;
        this.plugin = plugin;
        this.initialize();
    }

    private void initialize() {
        final FileConfiguration fileConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
        this.name = ((fileConfig.get("name") == null) ? Main.defaultName : fileConfig.getString("name"));
        this.slots = ((fileConfig.get("slots") == null) ? Main.defaultSlots : fileConfig.getInt("slots"));
        this.itemId = ((fileConfig.get("item-id") == null) ? Main.defaultItemId : fileConfig.getInt("item-id"));
        this.itemData = ((fileConfig.get("item-data") == null) ? Main.defaultItemData : ((short) fileConfig.getInt("item-data")));
        this.Backpack_ID = UUID.fromString(this.file.getName().substring(0, this.file.getName().length() - 4));
        this.User_ID = fileConfig.getString("bind-id");
        this.inventory = Bukkit.createInventory((InventoryHolder) new BackpackHolder(this.plugin, this.Backpack_ID), 54, ChatColor.translateAlternateColorCodes('&', this.getName()));
        try {
            this.inventory.setContents(InvUtil.loadInventory((ConfigurationSection) fileConfig));
        } catch (InvalidConfigurationException | IllegalArgumentException ex4) {

            ex4.printStackTrace();
        }
        for (int i = this.slots; i < this.inventory.getSize(); ++i) {
            this.inventory.setItem(i, Main.slotFiller.getItem());
        }
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

    public void updateBackpack() {
        this.saveBackpack();
        this.clearViewers();
        this.initialize();
    }

    public Inventory getPackInventory() {
        return this.inventory;
    }

    public void saveBackpack() {
        final FileConfiguration fileConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
        fileConfig.set("name", (Object) this.name);
        fileConfig.set("slots", (Object) this.getSlots());
        InvUtil.saveInventory(this.inventory, (ConfigurationSection) fileConfig, this.getSlots());
        try {
            fileConfig.save(this.file);
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }

    public void removeBackpack() {
        this.updateBackpack();
        this.plugin.unregisterBackpack(this);
        this.file.delete();
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(this.getItemId(), 1, this.getItemData());
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.getName());
        final List<String> lore = new ArrayList<String>();
        lore.add(" ");
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
        final FileConfiguration fileConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
        fileConfig.set("name", (Object) name);
        fileConfig.set("slots", (Object) this.getSlots());
        InvUtil.saveInventory(this.inventory, (ConfigurationSection) fileConfig, this.getSlots());
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
        return this.slots;
    }

    public void updateSlots(final int slots) {
        this.clearViewers();
        final FileConfiguration fileConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
        fileConfig.set("name", (Object) this.name);
        fileConfig.set("slots", (Object) slots);
        InvUtil.saveInventory(this.inventory, (ConfigurationSection) fileConfig, this.getSlots());
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
