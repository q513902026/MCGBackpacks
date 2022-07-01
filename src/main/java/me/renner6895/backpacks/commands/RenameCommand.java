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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author xiaoyv_404
 */
@CommandPermission(value = "backpacks.edit.rename",type = CommandType.PLAYER)
public class RenameCommand extends HopeCommand {

    Main plugin = getPlugin();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("rename.error2", "&cError: For information on how to use this command, type /backpacks help rename")));
            return false;
        }
        final Player player = (Player) commandSender;
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (!this.plugin.itemIsBackpack(item)) {
            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("rename.error3", "&cError: You must be holding the backpack in your hand to rename it.")));
            return false;
        }
        final String name = strings[1].replaceAll("_", " ");
        final String backpackId = this.plugin.getNmsUtil().getStringTag(item, "backpack-item");
        final Backpack backpack = this.plugin.getBackpack(UUID.fromString(backpackId));
        backpack.updateName(name);
        final ItemStack newBPItem = backpack.getItem();
        newBPItem.setAmount(item.getAmount());
        player.getInventory().setItemInMainHand(newBPItem);
        commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + String.format(FormatTool.getFormatText("rename.succuse", "&7Backpack renamed to %s &7."), name)));
        return true;
    }
}
