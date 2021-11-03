package me.renner6895.backpacks.events;

import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.objects.BackpackHolder;
import me.renner6895.backpacks.objects.PluginPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class InventoryEvents implements Listener {
    private Main plugin;

    public InventoryEvents(final Main plugin) {
        this.plugin = plugin;
    }

    private boolean checkOwner(final ItemStack item, final Player p) {
        return (p.hasPermission("backpacks.admin.viewall") | p.hasPermission("backpacks.admin.view")) || p.getName().equals(this.plugin.getNmsUtil().getStringTag(item, "backpack-owner"));
    }
    private ItemStack getItemInHand(Player player,EquipmentSlot hand){
        switch(hand){
            case HAND:
                return player.getInventory().getItemInMainHand();
            case OFF_HAND:
                return player.getInventory().getItemInOffHand();
        }
        return null;
    }
    @EventHandler
    public void itemClickEvent(final PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) ) {
            final ItemStack item = getItemInHand(e.getPlayer(),e.getHand());
            if(item == null){return;}
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
                e.getInventory().forEach(x->sendBackBackpackItemStack(x,e.getPlayer()));
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
    private void sendBackBackpackItemStack(ItemStack item, HumanEntity p){
        if (this.plugin.itemIsBackpack(item)){
            ItemStack backItem = item.clone();
            item.setAmount(0);
            if(p.getInventory().firstEmpty() == -1){
                World world = p.getWorld();
                final Location loc = p.getLocation().clone();
                world.dropItem(loc,backItem);
            }else
            {
                p.getInventory().addItem(backItem);
            }
        }
    }
}
