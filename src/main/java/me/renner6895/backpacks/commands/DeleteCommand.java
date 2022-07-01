package me.renner6895.backpacks.commands;import me.hope.core.CommandType;import me.hope.core.inject.annotation.command.CommandPermission;import me.renner6895.backpacks.Main;import me.renner6895.backpacks.commands.abstractclass.HopeCommand;import me.renner6895.backpacks.objects.Backpack;import me.renner6895.backpacks.tools.ColorTool;import me.renner6895.backpacks.tools.FormatTool;import org.bukkit.command.Command;import org.bukkit.command.CommandSender;import org.bukkit.entity.Player;import org.bukkit.inventory.ItemStack;import java.util.UUID;/** * @author xiaoyv_404 */@CommandPermission(value = "backpacks.edit.delete",type = CommandType.PLAYER)public class DeleteCommand extends HopeCommand {    Main plugin = getPlugin();    @Override    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {        final Player player = (Player) commandSender;        final ItemStack item = player.getInventory().getItemInMainHand();        if (this.plugin.itemIsBackpack(item)) {            final String backpackId = this.plugin.getNmsUtil().getStringTag(item, "backpack-item");            final Backpack backpack = this.plugin.getBackpack(UUID.fromString(backpackId));            backpack.removeBackpack();            item.setAmount(0);            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("delete.succuse", "&7The backpack has been delete!")));        } else {            commandSender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("delete.error2", "&cYou must be holding a backpack for this to work!")));        }        return true;    }}