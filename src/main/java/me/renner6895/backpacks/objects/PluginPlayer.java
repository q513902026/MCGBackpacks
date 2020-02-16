package me.renner6895.backpacks.objects;

import org.bukkit.entity.*;

import java.util.*;

import com.google.common.collect.*;

public class PluginPlayer {
    private Player player;
    private UUID currentBackpack;
    private List<Backpack> backpacks;

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

    public Player getPlayer() {
        return this.player;
    }

    public List<Backpack> getBackpacks() {
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
