package me.renner6895.backpacks.objects;

import me.hope.core.inject.annotation.Inject;
import me.renner6895.backpacks.Main;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class BackpackHolder implements InventoryHolder {
    @Inject
    private static Main plugin;
    private final UUID backpackId;
    private boolean isViewAll;

    public BackpackHolder(final UUID backpackId) {
        this.isViewAll = false;
        this.backpackId = backpackId;
    }

    public BackpackHolder setViewMenu(final boolean isViewAll) {
        this.isViewAll = isViewAll;
        return this;
    }

    public UUID getBackpack_id() {
        return this.backpackId;
    }

    public boolean isViewAll() {
        return this.isViewAll;
    }

    public Inventory getInventory() {
        return plugin.getBackpack(this.backpackId).getPackInventory();
    }
}
