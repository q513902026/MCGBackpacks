package me.renner6895.backpacks.events;

import me.renner6895.backpacks.*;
import org.bukkit.inventory.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;

import java.util.*;

import org.bukkit.event.*;
import me.renner6895.backpacks.objects.*;
import org.bukkit.*;
import org.bukkit.event.inventory.*;
import org.bukkit.plugin.*;

public class InventoryEvents implements Listener {
    private Main plugin;

    public InventoryEvents(final Main plugin) {
        this.plugin = plugin;
    }

    private boolean checkOwner(final ItemStack item, final Player p) {
        return (p.hasPermission("backpacks.admin.viewall") | p.hasPermission("backpacks.admin.view")) || p.getName().equals(this.plugin.getNmsUtil().getStringTag(item, "backpack-owner"));
    }

    @EventHandler
    public void itemClickEvent(final PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getPlayer().getItemInHand() != null) {
            final ItemStack item = e.getPlayer().getItemInHand();
            if (this.plugin.itemIsBackpack(item)) {
                e.setCancelled(true);
                final String backpackId = this.plugin.getNmsUtil().getStringTag(item, "backpack-item");
                if (this.checkOwner(item, e.getPlayer())) {
                    final Backpack backpack = this.plugin.getBackpack(UUID.fromString(backpackId));
                    final PluginPlayer pluginPlayer = this.plugin.getPluginPlayer(e.getPlayer().getName());
                    pluginPlayer.setCurrentBackpack(backpack.getUniqueId());
                    e.getPlayer().openInventory(backpack.getPackInventory());
                }
            }
        }
    }

    @EventHandler
    public void inventoryClickEvent(final InventoryClickEvent e) {
        final PluginPlayer pluginPlayer = this.plugin.getPluginPlayer(e.getWhoClicked().getName());
        if (e.getInventory().getHolder() instanceof BackpackHolder) {
            final BackpackHolder holder = (BackpackHolder) e.getInventory().getHolder();
            if (!holder.isViewAll()) {
                final ItemStack item = e.getCurrentItem();
                if (item != null && this.plugin.itemIsBackpack(item)) {
                    e.setCancelled(true);
                    return;
                }
                final Backpack backpack = this.plugin.getBackpack(holder.getBackpackid());
                if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST && e.getSlot() >= backpack.getSlots()) {
                    e.setCancelled(true);
                }
            }
        }
        if (ChatColor.stripColor(e.getInventory().getName()).startsWith("Backpacks - Viewing")) {
            e.setCancelled(true);
            if (this.plugin.itemIsBackpack(e.getCurrentItem())) {
                e.getWhoClicked().getInventory().addItem(new ItemStack[]{e.getCurrentItem()});
            }
        }
    }

    @EventHandler
    public void inventoryCloseEvent(final InventoryCloseEvent e) {
        final PluginPlayer pluginPlayer = this.plugin.getPluginPlayer(e.getPlayer().getName());
        if (e.getInventory().getHolder() instanceof BackpackHolder) {
            final BackpackHolder holder = (BackpackHolder) e.getInventory().getHolder();
            if (!holder.isViewAll()) {
                final Backpack backpack = this.plugin.getBackpack(holder.getBackpackid());
                this.plugin.getServer().getScheduler().runTask((Plugin) this.plugin, (Runnable) new Runnable() {
                    @Override
                    public void run() {
                        pluginPlayer.setCurrentBackpack(null);
                    }
                });
                backpack.saveBackpack();
            }
        }
    }
}
