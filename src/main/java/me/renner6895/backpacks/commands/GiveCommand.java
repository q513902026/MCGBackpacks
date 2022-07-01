package me.renner6895.backpacks.commands;

import me.hope.core.inject.annotation.command.CommandPermission;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.tools.ColorTool;
import me.renner6895.backpacks.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author xiaoyv_404
 */
@CommandPermission("backpacks.admin.give")
public class GiveCommand extends HopeCommand {
    Main plugin = getPlugin();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage(ColorTool.color(plugin.getPrefix() + FormatTool.getFormatText("give.error", "&cError: For information on how to use this command, type /backpacks help give")));
            return false;
        }
        final Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + String.format(FormatTool.getFormatText("give.error2", "&7The player %s is not online"), args[0])));
            return false;
        }
        CreateCommand.createBackpack(player, args);
        commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + String.format(FormatTool.getFormatText("give.succuse", "&7New Backpack given to %s ."), args[0])));
        return true;
    }
}
