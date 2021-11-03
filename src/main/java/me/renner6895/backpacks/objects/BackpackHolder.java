package me.renner6895.backpacks.objects;

import me.renner6895.backpacks.Main;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class BackpackHolder implements InventoryHolder {
    private Main plugin;
    private UUID backpackid;
    private boolean isViewAll;

    public BackpackHolder(final Main plugin, final UUID backpackid) {
        this.isViewAll = false;
        this.plugin = plugin;
        this.backpackid = backpackid;
    }

    public BackpackHolder setViewMenu(final boolean isViewAll) {
        this.isViewAll = isViewAll;
        return this;
    }

    public UUID getBackpackid() {
        return this.backpackid;
    }

    public boolean isViewAll() {
        return this.isViewAll;
    }

    public Inventory getInventory() {
        return this.plugin.getBackpack(this.backpackid).getPackInventory();
    }
}
