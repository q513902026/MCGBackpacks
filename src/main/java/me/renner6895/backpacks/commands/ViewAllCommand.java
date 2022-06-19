package me.renner6895.backpacks.commands;

import me.hope.core.CommandType;
import me.hope.core.inject.annotation.Inject;
import me.hope.core.inject.annotation.command.CommandPermission;
import me.renner6895.backpacks.BackPackCache;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.objects.BackpackHolder;
import me.renner6895.backpacks.tools.ColorTool;
import me.renner6895.backpacks.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.TreeMap;

/**
 * @author xiaoyv_404
 */
@CommandPermission(value = "backpacks.admin.viewall",type = CommandType.PLAYER)
public class ViewAllCommand extends HopeCommand {

    Main plugin = getPlugin();
    @Inject
    private static BackPackCache backPackCache;
    private TreeMap<Double, Backpack> orderedBackpackMap;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (this.orderedBackpackMap == null || this.orderedBackpackMap.size() != backPackCache.getBackpackMap().size()) {
            this.orderedBackpackMap = new TreeMap<>();
            for (final Backpack bp2 : backPackCache.getBackpackMap().values()) {
                double d2 = bp2.getSlots();
                for (boolean f2 = false; this.orderedBackpackMap.get(d2) != null && !f2; d2 += 0.001) {
                }
                this.orderedBackpackMap.put(d2, bp2);
            }
        }
        int page = 1;
        if (strings.length > 1) {
            try {
                page = Integer.parseInt(strings[1]);
            } catch (NumberFormatException ex2) {
            }
        }
        final Inventory inv2 = Bukkit.createInventory(new BackpackHolder(null).setViewMenu(true), 54, ColorTool.color(String.format(FormatTool.getFormatText("viewall.succuse", "Backpacks - &4Viewing All &8page %s"), page)));
        int counter2 = 0;
        for (final Backpack backpack : this.orderedBackpackMap.values()) {
            if (counter2 >= (page - 1) * 54 && counter2 < page * 54) {
                inv2.addItem(new ItemStack[]{backpack.getItem()});
            }
            if (++counter2 >= page * 54) {
                break;
            }
        }
        ((Player) commandSender).openInventory(inv2);
        Main.log.info("玩家<" + commandSender.getName() + "> 发起了查询所有背包的命令");
        return true;
    }
}
