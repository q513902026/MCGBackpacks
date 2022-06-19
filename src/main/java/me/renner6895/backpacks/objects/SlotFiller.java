package me.renner6895.backpacks.objects;

import me.hope.core.inject.annotation.Inject;
import me.renner6895.backpacks.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import org.bukkit.inventory.meta.*;

public class SlotFiller {
    @Inject
    private static Main plugin;

    private ItemStack item;

    public SlotFiller() {
        //refresh();
    }

    public void refresh() {
        //noinspection deprecation
        this.item = new ItemStack(plugin.getConfig().getInt("slot-filler.item-id"), 1, (short) plugin.getConfig().getInt("slot-filler.item-data"));
        final ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("slot-filler.name")));
        this.item.setItemMeta(itemMeta);
    }

    public ItemStack getItem() {
        return this.item;
    }
}
