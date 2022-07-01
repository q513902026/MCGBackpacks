package me.renner6895.backpacks.commands;

import com.google.common.collect.Lists;
import me.hope.core.CommandType;
import me.hope.core.inject.annotation.command.CommandPermission;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.tools.ColorTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CommandPermission(value = "backpacks.admin.info",type = CommandType.PLAYER)
public class InfoCommand extends HopeCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        Player sendPlayer = (Player)commandSender;
        Optional<Backpack> backpackOptional = Optional.empty();

        if (args.length == 0 ){
            backpackOptional = getPlugin().getBackpackByItem((sendPlayer.getInventory().getItemInMainHand()));
        }else if (args.length == 1){
            backpackOptional = getPlugin().getBackpackByUUIDString(args[0]);
        }
        if (backpackOptional.isPresent()){
            Backpack backpack = backpackOptional.get();
            List<String> backpackInfo = Lists.newArrayList();
            backpackInfo.add("名称 "+backpack.getName());
            backpackInfo.add("UUID "+backpack.getUniqueId());
            backpackInfo.add("容量 " + backpack.getSlots());
            backpackInfo.add("物品材质 " + backpack.getItemId()+":"+backpack.getItemData());
            for (String info : backpackInfo) {
                sendPlayer.sendMessage(ColorTool.color(getPlugin().getPrefix()+": "+info));
            }
            return true;
        }else{
            if (args.length == 0){
                sendPlayer.sendMessage(ColorTool.color(getPlugin().getPrefix() + ": 手持物品并不是已注册的背包"));
            }else if (args.length == 1){
                sendPlayer.sendMessage(ColorTool.color(getPlugin().getPrefix() + ": 不存在或不合法的UUID"));
            }
        }
        return false;
    }
}
