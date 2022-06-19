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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length <= 1) {
            final List<String> list2 = new ArrayList<>();
            list2.add(ColorTool.color("&3&m----------------------&3[&bBackpacks&3]&m---------------------"));
            list2.add(ColorTool.color("&3This plugin is maintained and developed by TheRealJeremy."));
            list2.add(ColorTool.color("&3Modified By HopeAsd , Color_yr."));
            if (commandSender.hasPermission("backpacks.edit.rename")) {
                list2.add(ColorTool.color("&3/backpack rename {name}"));
                list2.add(ColorTool.color("&7/backpack help rename"));
            }
            if (commandSender.hasPermission("backpacks.edit.reslot")) {
                list2.add(ColorTool.color("&3/backpack reslot {int}"));
                list2.add(ColorTool.color("&7/backpack help reslot"));
            }
            if (commandSender.hasPermission("backpacks.edit.clone")) {
                list2.add(ColorTool.color("&3/backpack clone"));
                list2.add(ColorTool.color("&7/backpack help clone"));
            }
            if (commandSender.hasPermission("backpacks.admin.create")) {
                list2.add(ColorTool.color("&3/backpack create <slots> <name> <item>"));
                list2.add(ColorTool.color("&7/backpack help create"));
            }
            if (commandSender.hasPermission("backpacks.admin.give")) {
                list2.add(ColorTool.color("&3/backpack give {player} <slots> <name> <item>"));
                list2.add(ColorTool.color("&7/backpack help give"));
            }
            if (commandSender.hasPermission("backpacks.admin.viewall")) {
                list2.add(ColorTool.color("&3/backpack viewall <page>"));
                list2.add(ColorTool.color("&7/backpack help viewall"));
            }
            if (commandSender.hasPermission("backpacks.admin.find")) {
                list2.add(ColorTool.color("&3/backpack find {player}"));
                list2.add(ColorTool.color("&7/backpack help find"));
            }
            if (commandSender.hasPermission("backpacks.admin.view")) {
                list2.add(ColorTool.color("&3/backpack view {player} <page>"));
                list2.add(ColorTool.color("&7/backpack help view"));
            }
            if (commandSender instanceof ConsoleCommandSender) {
                list2.add(ColorTool.color("&3/backpack rebuildCache"));
            }
            list2.add(ColorTool.color("&3&m-----------------------------------------------------"));
            for (final String s1 : list2) {
                commandSender.sendMessage(s1);
            }
            return true;
        }
        if (strings[1].equalsIgnoreCase("rename")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks rename {name}"));
            commandSender.sendMessage(ColorTool.color("&7- Rename a backpack you are holding in your hand!"));
            commandSender.sendMessage(ColorTool.color("&7- To use spaces in the name, use an underscore, Ex: \"My_Backpack_Name\"."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (strings[1].equalsIgnoreCase("reslot")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks reslot {int}"));
            commandSender.sendMessage(ColorTool.color("&7- Change the slots of a backpack you are holding in your hand!"));
            commandSender.sendMessage(ColorTool.color("&7- The slot number must be an integer from 1 to 54."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (strings[1].equalsIgnoreCase("clone")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks clone"));
            commandSender.sendMessage(ColorTool.color("&7- Creates an extra copy of the backpack!"));
            commandSender.sendMessage(ColorTool.color("&7- Useful for giving to friends, but careful who you trust!"));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (strings[1].equalsIgnoreCase("create")) {
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
        if (strings[1].equalsIgnoreCase("give")) {
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
        if (strings[1].equalsIgnoreCase("find")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpack find {player}"));
            commandSender.sendMessage(ColorTool.color("&7- find player backpacks List!"));
            commandSender.sendMessage(ColorTool.color("&7- The {player} argument is required for this command!"));
            commandSender.sendMessage(ColorTool.color("&7- Ex: \"/bp find TheRealJeremy \"."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (strings[1].equalsIgnoreCase("view")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks view {player} <page>"));
            commandSender.sendMessage(ColorTool.color("&7- View player the backpacks on the server!"));
            commandSender.sendMessage(ColorTool.color("&7- The {player} argument is required for this command!"));
            commandSender.sendMessage(ColorTool.color("&7- Click on a backpack to receive a copy of it."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        if (strings[1].equalsIgnoreCase("viewall")) {
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            commandSender.sendMessage(ColorTool.color("&3/backpacks viewall <page>"));
            commandSender.sendMessage(ColorTool.color("&7- View all the backpacks on the server!"));
            commandSender.sendMessage(ColorTool.color("&7- Click on a backpack to receive a copy of it."));
            commandSender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
            return true;
        }
        return true;
    }
}
