package me.renner6895.backpacks.events;

import me.hope.core.inject.annotation.Inject;
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
    @Inject
    private static Main plugin;

    public InventoryEvents() {

    }

    private boolean checkOwner(final ItemStack item, final Player p) {
        boolean hasOwner = plugin.getNmsUtil().hasKey(item,"backpack-owner");
        boolean isAdmin = (p.hasPermission("backpacks.admin.viewall") | p.hasPermission("backpacks.admin.view"));
        if (hasOwner){
            boolean isOwner = p.getName().equals(plugin.getNmsUtil().getStringTag(item, "backpack-owner"));
            return (isAdmin || isOwner);

        }else{
            return isAdmin;
        }
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
            if (plugin.itemIsBackpack(item)) {
                e.setCancelled(true);
                final String backpackId = plugin.getNmsUtil().getStringTag(item, "backpack-item");
                if (this.checkOwner(item, e.getPlayer())) {
                    final Backpack backpack = plugin.getBackpack(UUID.fromString(backpackId));
                    final PluginPlayer pluginPlayer = plugin.getPluginPlayer(e.getPlayer().getName());
                    pluginPlayer.setCurrentBackpack(backpack.getUniqueId());
                    e.getPlayer().openInventory(backpack.getPackInventory());
                }
            }
        }
    }

    @EventHandler
    public void inventoryClickEvent(final InventoryClickEvent e) {
        final PluginPlayer pluginPlayer = plugin.getPluginPlayer(e.getWhoClicked().getName());
        if (e.getInventory().getHolder() instanceof BackpackHolder) {
            final BackpackHolder holder = (BackpackHolder) e.getInventory().getHolder();
            if (!holder.isViewAll()) {
                final ItemStack item = e.getCurrentItem();
                if (item != null && plugin.itemIsBackpack(item)) {
                    e.setCancelled(true);
                    return;
                }
                final Backpack backpack = plugin.getBackpack(holder.getBackpack_id());
                if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST && e.getSlot() >= backpack.getSlots()) {
                    e.setCancelled(true);
                }
            }
        }
        if (ChatColor.stripColor(e.getInventory().getName()).startsWith("Backpacks - Viewing")) {
            e.setCancelled(true);
            if (plugin.itemIsBackpack(e.getCurrentItem())) {
                e.getWhoClicked().getInventory().addItem(new ItemStack[]{e.getCurrentItem()});
            }
        }
    }

    @EventHandler
    public void inventoryCloseEvent(final InventoryCloseEvent e) {
        final PluginPlayer pluginPlayer = plugin.getPluginPlayer(e.getPlayer().getName());
        if (e.getInventory().getHolder() instanceof BackpackHolder) {
            final BackpackHolder holder = (BackpackHolder) e.getInventory().getHolder();
            if (!holder.isViewAll()) {
                final Backpack backpack = plugin.getBackpack(holder.getBackpack_id());
                e.getInventory().forEach(x-> sendBackpackItemStack(x,e.getPlayer()));
                plugin.getServer().getScheduler().runTask((Plugin) plugin, (Runnable) new Runnable() {
                    @Override
                    public void run() {
                        pluginPlayer.setCurrentBackpack(null);
                    }
                });
                backpack.saveBackpack();
            }
        }
    }

    private void sendBackpackItemStack(ItemStack item, HumanEntity p){
        if (plugin.itemIsBackpack(item)){
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
