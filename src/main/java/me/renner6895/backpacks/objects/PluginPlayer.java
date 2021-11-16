package me.renner6895.backpacks.objects;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public class PluginPlayer {
    private Player player;
    private UUID currentBackpack;
    private List<Backpack> backpacks;
    private TreeMap<Double, Backpack> orderedPlayerBackpackMap;

    public PluginPlayer(final Player player) {
        this.backpacks = Lists.newArrayList();
        this.player = player;
        this.initialize();
    }

    private void initialize() {
    }

    public void removal() {
        this.backpacks.clear();
        this.currentBackpack = null;
    }
    public List<Backpack> updateBackpackList(){
        if (this.orderedPlayerBackpackMap == null || this.orderedPlayerBackpackMap.size() != backpacks.size()) {
            this.orderedPlayerBackpackMap = new TreeMap<Double, Backpack>();
            for (final Backpack bp : backpacks) {
                double d = bp.getSlots();
                for (boolean f = false; this.orderedPlayerBackpackMap.get(d) != null && !f; d += 0.001) {
                }
                this.orderedPlayerBackpackMap.put(d, bp);
            }
        }
        return Lists.newArrayList(this.orderedPlayerBackpackMap.values());
    }

    public Inventory getBackpackListInv(Inventory inv,int page){
        int counter = 0;
        for (final Backpack backpack3 : this.orderedPlayerBackpackMap.values()) {
            if (counter >= (page - 1) * 54 && counter < page * 54) {
                inv.addItem(new ItemStack[]{backpack3.getItem()});
            }
            if (++counter >= page * 54) {
                break;
            }
        }
        return inv;
    }
    public Player getPlayer() {
        return this.player;
    }

    public List<Backpack> getBackpacks() {
        updateBackpackList();
        return this.backpacks;
    }

    public UUID getCurrentBackpack() {
        return this.currentBackpack;
    }

    public void setCurrentBackpack(final UUID currentBackpack) {
        this.currentBackpack = currentBackpack;
    }

    public void addBackpack(final Backpack value) {
        this.backpacks.add(value);
    }

    public void removeBackpack(final Backpack value) {
        this.backpacks.remove(value);
    }
}
