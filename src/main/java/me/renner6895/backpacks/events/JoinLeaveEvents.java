package me.renner6895.backpacks.events;

import me.hope.core.inject.annotation.Inject;
import me.renner6895.backpacks.*;
import me.renner6895.backpacks.objects.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class JoinLeaveEvents implements Listener {
    @Inject
    private static Main plugin;

    public JoinLeaveEvents() {

    }

    @EventHandler
    public void joinEvent(final PlayerJoinEvent e) {
        final PluginPlayer pluginPlayer = new PluginPlayer(e.getPlayer());
        this.plugin.registerPlayer(pluginPlayer);
    }

    @EventHandler
    public void leaveEvent(final PlayerQuitEvent e) {
        final PluginPlayer pluginPlayer = this.plugin.getPluginPlayer(e.getPlayer().getName());
        pluginPlayer.removal();
        this.plugin.unregisterPlayer(pluginPlayer);
    }
}
