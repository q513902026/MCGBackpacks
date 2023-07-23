package me.renner6895.backpacks.commands.abstractclass;

import me.hope.core.inject.annotation.Inject;
import me.renner6895.backpacks.Main;
import org.bukkit.command.CommandExecutor;

import java.util.logging.Logger;

/**
 * @author xiaoyv_404
 */
public abstract class HopeCommand implements CommandExecutor {

    @Inject
    private static Main plugin;
    @Inject
    private static Logger logger;

    protected static Logger getLogger() {
        return logger;
    }

    protected static Main getPlugin() {
        return plugin;
    }

}