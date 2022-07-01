package me.renner6895.backpacks.commands;

import me.hope.core.CommandType;
import me.hope.core.inject.annotation.command.CommandPermission;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.objects.BackpackHolder;
import me.renner6895.backpacks.objects.PluginPlayer;
import me.renner6895.backpacks.tools.ColorTool;
import me.renner6895.backpacks.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * @author xiaoyv_404
 */
@CommandPermission(value = "backpacks.admin.view", type = CommandType.PLAYER)
public class ViewCommand extends HopeCommand {

    Main plugin = getPlugin();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("view.error", "&cError: You must be a player name use this command.")));
            return false;
        }
        final String bindID = strings[1];
        if (this.plugin.getPluginPlayer(bindID) == null) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("view.error3", "&cError: player must be online.")));
            return false;
        }
        PluginPlayer pluginPlayer = this.plugin.getPluginPlayer(bindID);
        final List<Backpack> backpacks = pluginPlayer.getBackpacks();
        if (backpacks.isEmpty()) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("view.error2", "&cError: You must be a vaild player name use this command.")));
            return false;
        }
        pluginPlayer.updateBackpackList();
        int page2 = 1;
        if (strings.length > 2) {
            try {
                page2 = Integer.parseInt(strings[2]);
            } catch (NumberFormatException ignored) {
            }
        }
        final Inventory inv = pluginPlayer.getBackpackListInv(Bukkit.createInventory(new BackpackHolder(null).setViewMenu(true), 54, ColorTool.color(String.format(FormatTool.getFormatText("viewall.succuse", "Backpacks - &4Viewing %s &8page %s"), bindID, page2))), page2);
        ((Player) commandSender).openInventory(inv);
        Main.log.info("玩家<" + commandSender.getName() + "> 正在查询玩家<" + bindID + ">的所有背包");
        return true;
    }
}
