package me.renner6895.backpacks.commands;

import me.hope.core.CommandType;
import me.hope.core.inject.annotation.command.CommandPermission;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.tools.ColorTool;
import me.renner6895.backpacks.tools.FormatTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author xiaoyv_404
 */
@CommandPermission(value = "backpacks.edit.clone",type = CommandType.PLAYER)
public class CloneCommand extends HopeCommand {
    Main plugin = getPlugin();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        final Player player = (Player) commandSender;
        if (this.plugin.itemIsBackpack(player.getInventory().getItemInMainHand())) {
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() + 1);
            commandSender.sendMessage(ColorTool.color(plugin.getPrefix() + FormatTool.getFormatText("clone.succuse", "&7The backpack item has been cloned!")));
        } else {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("clone.error2", "&cYou must be holding a backpack for this to work!")));
        }
        return true;
    }
}
