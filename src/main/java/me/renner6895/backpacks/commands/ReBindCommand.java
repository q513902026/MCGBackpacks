package me.renner6895.backpacks.commands;

import me.hope.core.inject.annotation.command.CommandAlias;
import me.hope.core.inject.annotation.command.CommandPermission;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.tools.ColorTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * @author HopeAsd
 */
@CommandPermission(value = "backpacks.admin.rebind")
@CommandAlias("rbind")
public class ReBindCommand extends HopeCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Optional<Backpack> backpackOptional = Optional.empty();
        Optional<Player> bindPlayerOptional = Optional.empty();
        Optional<ItemStack> playerItemStackOptional = Optional.empty();
        if (args.length == 1){
            if (commandSender instanceof Player){
                Player commandPlayer = (Player) commandSender;
                ItemStack mainItemStack = commandPlayer.getInventory().getItemInMainHand();
                playerItemStackOptional = Optional.of(mainItemStack);
                backpackOptional = getPlugin().getBackpackByItem(mainItemStack);
                bindPlayerOptional = Optional.of(getPlugin().getServer().getPlayerExact(args[0]));
            }
        }else if (args.length >= 2 ){
            try{
                backpackOptional = Optional.of(getPlugin().getBackpack(UUID.fromString(args[0])));
            }catch (IllegalArgumentException exception){
                commandSender.sendMessage(ColorTool.color(getPlugin().getPrefix()+": 不合法的UUID "));
                return false;
            }
            if(args.length == 3){
                boolean ignoreCheckPlayerOnline = Boolean.parseBoolean(args[2]);
                if (ignoreCheckPlayerOnline){
                    if (backpackOptional.isPresent()){
                        rebindBackpack(backpackOptional.get(),args[1],commandSender);
                        return true;
                    }else{
                        return false;
                    }
                }
            }
            bindPlayerOptional = Optional.of(getPlugin().getServer().getPlayerExact(args[1]));

        }
        if (backpackOptional.isPresent() && bindPlayerOptional.isPresent()){
            Backpack backpack = backpackOptional.get();
            rebindBackpack(backpack,bindPlayerOptional.get().getName(),commandSender);
            if (playerItemStackOptional.isPresent()){
                playerItemStackOptional.get().setAmount(0);
                ((Player) commandSender).getInventory().addItem(backpack.getItem());
            }
            return true;
        }else{
            commandSender.sendMessage(ColorTool.color(getPlugin().getPrefix()+": 玩家不在线|不存在的UUID"));
        }
        return false;
    }
    private void rebindBackpack(Backpack backpack,String bindName,CommandSender sender){
        backpack.rebind(bindName);
        sender.sendMessage(ColorTool.color(getPlugin().getPrefix()+":成功将背包绑定到 "+bindName+" ."));
    }
}
