package me.renner6895.backpacks.commands;

import me.hope.core.CommandType;
import me.hope.core.inject.annotation.command.CommandPermission;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author xiaoyv_404
 */
@CommandPermission(value = "backpacks.admin.view", type = CommandType.CONSOLE)
public class RebuildCacheCommand extends HopeCommand {

    Main plugin = getPlugin();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> Main.INSTANCE().buildCache());
        Main.log.info("正在异步建立缓存...");
        return true;
    }
}
