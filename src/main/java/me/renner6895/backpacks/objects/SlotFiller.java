package me.renner6895.backpacks.objects;

import me.renner6895.backpacks.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.inventory.meta.*;

public class SlotFiller {
    private Main plugin;
    private ItemStack item;

    public SlotFiller(final Main plugin) {
        this.plugin = plugin;
        this.initialize();
    }

    private void initialize() {
        this.refresh();
    }

    public void refresh() {
        //noinspection deprecation
        this.item = new ItemStack(this.plugin.getConfig().getInt("slot-filler.item-id"), 1, (short) this.plugin.getConfig().getInt("slot-filler.item-data"));
        final ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("slot-filler.name")));
        this.item.setItemMeta(itemMeta);
    }

    public ItemStack getItem() {
        return this.item;
    }
}
