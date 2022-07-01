package me.renner6895.backpacks.commands;

import me.hope.core.CommandType;
import me.hope.core.inject.annotation.command.CommandPermission;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.tools.ColorTool;
import me.renner6895.backpacks.tools.FormatTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author xiaoyv_404
 * todo 修改为onlyPlayer
 */
@CommandPermission(value = "backpacks.edit.reslot", type = CommandType.PLAYER)
public class ReslotCommand extends HopeCommand {

    Main plugin = getPlugin();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("reslot.error2", "&cError: For information on how to use this command, type /backpacks help reslot")));
            return false;
        }
        int page;
        try {
            page = Integer.parseInt(strings[1]);
        } catch (NumberFormatException var18) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("reslot.error3", "&cError: For information on how to use this command, type /backpacks help reslot")));
            return false;
        }
        if (page < 1 || page > 54) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("reslot.error3", "&cError: For information on how to use this command, type /backpacks help reslot")));
            return false;
        }
        final Player player2 = (Player) commandSender;
        final ItemStack item2 = player2.getInventory().getItemInMainHand();
        if (!this.plugin.itemIsBackpack(item2)) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("reslot.error4", "&cError: You must be holding the backpack in your hand to reslot it.")));
            return false;
        }
        final String backpackId = ((MemorySection) this.plugin.getNmsUtil().getTag(item2)).getString("backpack-item");
        final Backpack backpack = this.plugin.getBackpack(UUID.fromString(backpackId));
        backpack.updateSlots(page);
        final ItemStack newBPItem = backpack.getItem();
        newBPItem.setAmount(item2.getAmount());
        player2.getInventory().setItemInMainHand(newBPItem);
        commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + String.format(FormatTool.getFormatText("reslot.succuse", "&7Backpack reslotted to  %s slots&7."), page)));
        return true;
    }
}
