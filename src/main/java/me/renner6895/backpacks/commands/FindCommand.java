package me.renner6895.backpacks.commands;

import com.google.common.collect.Lists;
import me.hope.core.inject.annotation.CommandPermission;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.tools.ColorTool;
import me.renner6895.backpacks.tools.FormatTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author xiaoyv_404
 */
@CommandPermission("backpacks.admin.find")
public class FindCommand extends HopeCommand {

    Main plugin = getPlugin();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 2) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("find.error", "&cError: You must be a player name use this command.")));
            return false;
        }
        final String bindID = strings[1];
        final List<Backpack> backpacks = this.plugin.getPluginPlayer(bindID) == null ? findOfflinePlayerBackpacks(bindID) : this.plugin.getPluginPlayer(bindID).getBackpacks();
        if (backpacks.isEmpty()) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("find.error2", "&cError: You must be a vaild player name use this command.")));
            return false;
        }
        final List<String> list = Lists.newArrayList();
        for (final Backpack backpack : backpacks) {
            list.add("Name:{" + backpack.getName() + "} | UUID:{" + backpack.getUniqueId().toString() + "} | Slot:{" + backpack.getSlots() + "} ");
        }
        commandSender.sendMessage(ColorTool.color(String.format("&3&m-----------------------%s&3&m---------------------------", bindID)));
        for (final String message : list) {
            commandSender.sendMessage(message);
        }
        commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
        return false;
    }

    private List<Backpack> findOfflinePlayerBackpacks(String bindID) {
        List<Backpack> backpacks = Lists.newArrayList();
        for (Backpack backpack : plugin.getBackpackMap().values()) {
            if (Backpack.isOwner(backpack, bindID)) {
                backpacks.add(backpack);
            }
        }
        return backpacks;
    }
}
