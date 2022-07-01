package me.renner6895.backpacks.commands;

import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.tools.ColorTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoyv_404
 */
public class HelpCommand extends HopeCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            final List<String> backpackHelpTextList = new ArrayList<>();
            backpackHelpTextList.add(ColorTool.color("&3&m----------------------&3[&bBackpacks&3]&m---------------------"));
            backpackHelpTextList.add(ColorTool.color("&3This plugin is maintained and developed by TheRealJeremy."));
            backpackHelpTextList.add(ColorTool.color("&3Modified By HopeAsd , Color_yr."));
            if (commandSender.hasPermission("backpacks.edit.rename")) {
                backpackHelpTextList.add(ColorTool.color("&3/backpack rename {name}"));
                backpackHelpTextList.add(ColorTool.color("&7/backpack help rename"));
            }
            if (commandSender.hasPermission("backpacks.edit.reslot")) {
                backpackHelpTextList.add(ColorTool.color("&3/backpack reslot {int}"));
                backpackHelpTextList.add(ColorTool.color("&7/backpack help reslot"));
            }
            if (commandSender.hasPermission("backpacks.edit.clone")) {
                backpackHelpTextList.add(ColorTool.color("&3/backpack clone"));
                backpackHelpTextList.add(ColorTool.color("&7/backpack help clone"));
            }
            if (commandSender.hasPermission("backpacks.admin.create")) {
                backpackHelpTextList.add(ColorTool.color("&3/backpack create <slots> <name> <item>"));
                backpackHelpTextList.add(ColorTool.color("&7/backpack help create"));
            }
            if (commandSender.hasPermission("backpacks.admin.give")) {
                backpackHelpTextList.add(ColorTool.color("&3/backpack give {player} <slots> <name> <item>"));
                backpackHelpTextList.add(ColorTool.color("&7/backpack help give"));
            }
            if (commandSender.hasPermission("backpacks.admin.viewall")) {
                backpackHelpTextList.add(ColorTool.color("&3/backpack viewall <page>"));
                backpackHelpTextList.add(ColorTool.color("&7/backpack help viewall"));
            }
            if (commandSender.hasPermission("backpacks.admin.find")) {
                backpackHelpTextList.add(ColorTool.color("&3/backpack find {player}"));
                backpackHelpTextList.add(ColorTool.color("&7/backpack help find"));
            }
            if (commandSender.hasPermission("backpacks.admin.view")) {
                backpackHelpTextList.add(ColorTool.color("&3/backpack view {player} <page>"));
                backpackHelpTextList.add(ColorTool.color("&7/backpack help view"));
            }
            if (commandSender instanceof ConsoleCommandSender) {
                backpackHelpTextList.add(ColorTool.color("&3/backpack rebuildCache"));
            }
            backpackHelpTextList.add(ColorTool.color("&3&m-----------------------------------------------------"));
            for (final String s1 : backpackHelpTextList) {
                commandSender.sendMessage(s1);
            }
            return true;
        }
        if (args[1].equalsIgnoreCase("rename")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks rename {name}"));
            commandSender.sendMessage(ColorTool.color("&7- Rename a backpack you are holding in your hand!"));
            commandSender.sendMessage(ColorTool.color("&7- To use spaces in the name, use an underscore, Ex: \"My_Backpack_Name\"."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (args[1].equalsIgnoreCase("reslot")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks reslot {int}"));
            commandSender.sendMessage(ColorTool.color("&7- Change the slots of a backpack you are holding in your hand!"));
            commandSender.sendMessage(ColorTool.color("&7- The slot number must be an integer from 1 to 54."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (args[1].equalsIgnoreCase("clone")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks clone"));
            commandSender.sendMessage(ColorTool.color("&7- Creates an extra copy of the backpack!"));
            commandSender.sendMessage(ColorTool.color("&7- Useful for giving to friends, but careful who you trust!"));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (args[1].equalsIgnoreCase("create")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpack create <slots> <name> <item>"));
            commandSender.sendMessage(ColorTool.color("&7- Create a backpack for yourself!"));
            commandSender.sendMessage(ColorTool.color("&7- The slots, name, and item are all optional, and will default if not given."));
            commandSender.sendMessage(ColorTool.color("&7- To use the slots option inclued \"slots:{int}\" in your command."));
            commandSender.sendMessage(ColorTool.color("&7- To use the name option inclued \"name:{name}\" in your command."));
            commandSender.sendMessage(ColorTool.color("&7- To use the item option inclued \"item:{item-id}:{item-data}\" in your command."));
            commandSender.sendMessage(ColorTool.color("&7- Ex: \"/bp create slots:14 name:&My_Backpack_Name item:35:9\"."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (args[1].equalsIgnoreCase("give")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpack give {player} <slots> <name> <item>"));
            commandSender.sendMessage(ColorTool.color("&7- Give a backpack to someone!"));
            commandSender.sendMessage(ColorTool.color("&7- The {player} argument is required for this command!"));
            commandSender.sendMessage(ColorTool.color("&7- The slots, name, and item are all optional, and will default if not given."));
            commandSender.sendMessage(ColorTool.color("&7- To use the slots option inclued \"slots:{int}\" in your command."));
            commandSender.sendMessage(ColorTool.color("&7- To use the name option inclued \"name:{name}\" in your command."));
            commandSender.sendMessage(ColorTool.color("&7- To use the item option inclued \"item:{item-id}:{item-data}\" in your command."));
            commandSender.sendMessage(ColorTool.color("&7- Ex: \"/bp give TheRealJeremy slots:14 name:&My_Backpack_Name item:35:9\"."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (args[1].equalsIgnoreCase("find")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpack find {player}"));
            commandSender.sendMessage(ColorTool.color("&7- find player backpacks List!"));
            commandSender.sendMessage(ColorTool.color("&7- The {player} argument is required for this command!"));
            commandSender.sendMessage(ColorTool.color("&7- Ex: \"/bp find TheRealJeremy \"."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (args[1].equalsIgnoreCase("view")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks view {player} <page>"));
            commandSender.sendMessage(ColorTool.color("&7- View player the backpacks on the server!"));
            commandSender.sendMessage(ColorTool.color("&7- The {player} argument is required for this command!"));
            commandSender.sendMessage(ColorTool.color("&7- Click on a backpack to receive a copy of it."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (args[1].equalsIgnoreCase("viewall")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks viewall <page>"));
            commandSender.sendMessage(ColorTool.color("&7- View all the backpacks on the server!"));
            commandSender.sendMessage(ColorTool.color("&7- Click on a backpack to receive a copy of it."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (args[1].equalsIgnoreCase("rebind")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks rebind <backpackItem> {bindPlayer} <ignoreCheckPlayerOnline>"));
            commandSender.sendMessage(ColorTool.color("&7- 把背包重新绑定到其他玩家."));
            commandSender.sendMessage(ColorTool.color("&7- 背包物品默认获取主手手持物品进行判断 如果没有背包物品 请输入背包的UUID."));
            commandSender.sendMessage(ColorTool.color("&7- 当参数 ignoreCheckPlayerOnline 为真时 会不检查玩家是否在线 而直接绑定."));
            commandSender.sendMessage(ColorTool.color("&7- 当参数 ignoreCheckPlayerOnline 仅在参数长度为3时生效."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        return true;
    }
}
