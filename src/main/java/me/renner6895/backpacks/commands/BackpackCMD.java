package me.renner6895.backpacks.commands;

import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.tools.ColorTool;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

public class BackpackCMD implements CommandExecutor {
    private Main plugin;

    public BackpackCMD(final Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length >= 1 && !args[0].equalsIgnoreCase("help")) {
            if (args.length <= 1) {
                final List<String> list2 = new ArrayList<String>();
                list2.add(ColorTool.color("&3&m----------------------&3[&bBackpacks&3]&m---------------------"));
                list2.add(ColorTool.color("&3This plugin is maintained and developed by TheRealJeremy."));
                list2.add(ColorTool.color("&3Modified By HopeAsd , Color_yr."));
                if (sender.hasPermission("backpacks.edit.rename")) {
                    list2.add(ColorTool.color("&3/backpack rename {name}"));
                    list2.add(ColorTool.color("&7/backpack help rename"));
                }
                if (sender.hasPermission("backpacks.edit.reslot")) {
                    list2.add(ColorTool.color("&3/backpack reslot {int}"));
                    list2.add(ColorTool.color("&7/backpack help reslot"));
                }
                if (sender.hasPermission("backpacks.edit.clone")) {
                    list2.add(ColorTool.color("&3/backpack clone"));
                    list2.add(ColorTool.color("&7/backpack help clone"));
                }
                if (sender.hasPermission("backpacks.admin.create")) {
                    list2.add(ColorTool.color("&3/backpack create <slots> <name> <item>"));
                    list2.add(ColorTool.color("&7/backpack help create"));
                }
                if (sender.hasPermission("backpacks.admin.give")) {
                    list2.add(ColorTool.color("&3/backpack give {player} <slots> <name> <item>"));
                    list2.add(ColorTool.color("&7/backpack help give"));
                }
                if (sender.hasPermission("backpacks.admin.viewall")) {
                    list2.add(ColorTool.color("&3/backpack viewall <page>"));
                    list2.add(ColorTool.color("&7/backpack help viewall"));
                }
                if (sender.hasPermission("backpacks.admin.find")) {
                    list2.add(ColorTool.color("&3/backpack find {player}"));
                    list2.add(ColorTool.color("&7/backpack help find"));
                }
                if (sender.hasPermission("backpacks.admin.view")) {
                    list2.add(ColorTool.color("&3/backpack view {player} <page>"));
                    list2.add(ColorTool.color("&7/backpack help view"));
                }
                if (sender instanceof ConsoleCommandSender) {
                    list2.add(ColorTool.color("&3/backpack rebuildCache"));
                }
                list2.add(ColorTool.color("&3&m-----------------------------------------------------"));
                for (final String s : list2) {
                    sender.sendMessage(s);
                }
                return false;
            }
            if (args[1].equalsIgnoreCase("rename")) {
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(ColorTool.color("&3/backpacks rename {name}"));
                sender.sendMessage(ColorTool.color("&7- Rename a backpack you are holding in your hand!"));
                sender.sendMessage(ColorTool.color("&7- To use spaces in the name, use an underscore, Ex: \"My_Backpack_Name\"."));
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("reslot")) {
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(ColorTool.color("&3/backpacks reslot {int}"));
                sender.sendMessage(ColorTool.color("&7- Change the slots of a backpack you are holding in your hand!"));
                sender.sendMessage(ColorTool.color("&7- The slot number must be an integer from 1 to 54."));
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("clone")) {
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(ColorTool.color("&3/backpacks clone"));
                sender.sendMessage(ColorTool.color("&7- Creates an extra copy of the backpack!"));
                sender.sendMessage(ColorTool.color("&7- Useful for giving to friends, but careful who you trust!"));
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("create")) {
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(ColorTool.color("&3/backpack create <slots> <name> <item>"));
                sender.sendMessage(ColorTool.color("&7- Create a backpack for yourself!"));
                sender.sendMessage(ColorTool.color("&7- The slots, name, and item are all optional, and will default if not given."));
                sender.sendMessage(ColorTool.color("&7- To use the slots option inclued \"slots:{int}\" in your command."));
                sender.sendMessage(ColorTool.color("&7- To use the name option inclued \"name:{name}\" in your command."));
                sender.sendMessage(ColorTool.color("&7- To use the item option inclued \"item:{item-id}:{item-data}\" in your command."));
                sender.sendMessage(ColorTool.color("&7- Ex: \"/bp create slots:14 name:&My_Backpack_Name item:35:9\"."));
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("give")) {
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(ColorTool.color("&3/backpack give {player} <slots> <name> <item>"));
                sender.sendMessage(ColorTool.color("&7- Give a backpack to someone!"));
                sender.sendMessage(ColorTool.color("&7- The {player} argument is required for this command!"));
                sender.sendMessage(ColorTool.color("&7- The slots, name, and item are all optional, and will default if not given."));
                sender.sendMessage(ColorTool.color("&7- To use the slots option inclued \"slots:{int}\" in your command."));
                sender.sendMessage(ColorTool.color("&7- To use the name option inclued \"name:{name}\" in your command."));
                sender.sendMessage(ColorTool.color("&7- To use the item option inclued \"item:{item-id}:{item-data}\" in your command."));
                sender.sendMessage(ColorTool.color("&7- Ex: \"/bp give TheRealJeremy slots:14 name:&My_Backpack_Name item:35:9\"."));
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("find")) {
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(ColorTool.color("&3/backpack find {player}"));
                sender.sendMessage(ColorTool.color("&7- find player backpacks List!"));
                sender.sendMessage(ColorTool.color("&7- The {player} argument is required for this command!"));
                sender.sendMessage(ColorTool.color("&7- Ex: \"/bp find TheRealJeremy \"."));
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("view")) {
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(ColorTool.color("&3/backpacks view {player} <page>"));
                sender.sendMessage(ColorTool.color("&7- View player the backpacks on the server!"));
                sender.sendMessage(ColorTool.color("&7- The {player} argument is required for this command!"));
                sender.sendMessage(ColorTool.color("&7- Click on a backpack to receive a copy of it."));
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("viewall")) {
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(ColorTool.color("&3/backpacks viewall <page>"));
                sender.sendMessage(ColorTool.color("&7- View all the backpacks on the server!"));
                sender.sendMessage(ColorTool.color("&7- Click on a backpack to receive a copy of it."));
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                return false;
            }
            return false;
        }
        return false;
    }
}
