package me.renner6895.backpacks.commands;

import me.renner6895.backpacks.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BackpackCMD implements CommandExecutor {
    private Main plugin;

    public BackpackCMD(final Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length >= 1 && !args[0].equalsIgnoreCase("help")) {

        }
        return false;
    }
}
