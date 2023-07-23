package me.renner6895.backpacks.events;

import me.hope.core.inject.annotation.Inject;
import me.renner6895.backpacks.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;

public class CraftingEvents implements Listener {
    @Inject
    private static Main plugin;

    public CraftingEvents() {
    }

    @EventHandler
    public void craftItem(final CraftItemEvent e) {
        ItemStack[] var5;
        for (int var4 = (var5 = e.getInventory().getContents()).length, var6 = 0; var6 < var4; ++var6) {
            final ItemStack item = var5[var6];
            if (plugin.itemIsBackpack(item)) {
                e.setResult(null);
            }
        }
    }
}
