package me.renner6895.backpacks.events;

import me.renner6895.backpacks.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;

public class CraftingEvents implements Listener {
    private Main plugin;

    public CraftingEvents(final Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void craftItem(final CraftItemEvent e) {
        ItemStack[] var5;
        for (int var4 = (var5 = e.getInventory().getContents()).length, var6 = 0; var6 < var4; ++var6) {
            final ItemStack item = var5[var6];
            if (this.plugin.itemIsBackpack(item)) {
                e.setResult((Event.Result) null);
            }
        }
    }
}
