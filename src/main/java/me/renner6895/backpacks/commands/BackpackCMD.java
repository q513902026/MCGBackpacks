package me.renner6895.backpacks.commands;

import com.google.common.collect.Lists;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.objects.BackpackHolder;
import me.renner6895.backpacks.objects.PluginPlayer;
import me.renner6895.backpacks.tools.ColorTool;
import me.renner6895.backpacks.tools.FormatTool;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class BackpackCMD implements CommandExecutor {
    private Main plugin;
    private TreeMap<Double, Backpack> orderedBackpackMap;
    private TreeMap<Double, Backpack> orderedPlayerBackpackMap;

    public BackpackCMD(final Main plugin) {
        this.plugin = plugin;
    }

    private String getFormatText(final String key, final String defval) {
        final FileConfiguration config = this.plugin.getConfig();
        if (config.getConfigurationSection("lang." + key) == null) {
            config.set("lang." + key, (Object) defval);
        }
        this.plugin.saveConfig();
        return config.getString("lang." + key);
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length >= 1 && !args[0].equalsIgnoreCase("help")) {
            if (args[0].equalsIgnoreCase("find")) {
                if (!this.checkPermission("backpacks.admin.find", sender, true)) {
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("find.error", "&cError: You must be a player name use this command.")));
                    return false;
                }
                final String bindID = args[1];
                final List<Backpack> backpacks = this.plugin.getPluginPlayer(bindID) == null ? findOfflinePlayerBackpacks(bindID) : this.plugin.getPluginPlayer(bindID).getBackpacks();
                if (backpacks.size() == 0) {
                    sender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("find.error2", "&cError: You must be a vaild player name use this command.")));
                    return false;
                }
                final List<String> list = Lists.newArrayList();
                for (final Backpack backpack : backpacks) {
                    list.add("Name:{" + backpack.getName() + "} | UUID:{" + backpack.getUniqueId().toString() + "} | Slot:{" + backpack.getSlots() + "} ");
                }
                sender.sendMessage(ColorTool.color(String.format("&3&m-----------------------%s&3&m---------------------------", bindID)));
                for (final String message : list) {
                    sender.sendMessage(message);
                }
                sender.sendMessage(ColorTool.color("&3&m-----------------------------------------------------"));
                return false;
            } else if (args[0].equalsIgnoreCase("view")) {
                if (!this.checkPermission("backpacks.admin.view", sender, true)) {
                    return false;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("view.error", "&cError: You must be a player to use this command.")));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("view.error", "&cError: You must be a player name use this command.")));
                    return false;
                }
                final String bindID = args[1];
                if (this.plugin.getPluginPlayer(bindID) == null) {
                    sender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("view.error3", "&cError: player must be online.")));
                    return false;
                }
                PluginPlayer pluginPlayer = this.plugin.getPluginPlayer(bindID);
                final List<Backpack> backpacks = pluginPlayer.getBackpacks();
                if (backpacks.size() == 0) {
                    sender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("view.error2", "&cError: You must be a vaild player name use this command.")));
                    return false;
                }
                pluginPlayer.updateBackpackList();
                int page2 = 1;
                if (args.length > 2) {
                    try {
                        page2 = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ex) {
                    }
                }
                final Inventory inv = pluginPlayer.getBackpackListInv(Bukkit.createInventory((InventoryHolder) new BackpackHolder(this.plugin, null).setViewMenu(true), 54, ColorTool.color(String.format(FormatTool.getFormatText("viewall.succuse", "Backpacks - &4Viewing %s &8page %s"), bindID, page2))), page2);
                ((Player) sender).openInventory(inv);
                Main.log.info("玩家<" + sender.getName() + "> 正在查询玩家<" + bindID + ">的所有背包");
                return false;
            } else if (args[0].equalsIgnoreCase("rebuildCache")) {
                if (!this.checkPermission("backpacks.admin.view", sender, true)) {
                    return false;
                }
                if (sender instanceof ConsoleCommandSender) {
                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> Main.INSTANCE().buildCache());
                    Main.log.info("正在异步建立缓存...");
                }
                return false;
            } else {
                if (!args[0].equalsIgnoreCase("viewall")) {
                    return false;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ColorTool.color(this.plugin.getPrefix() + FormatTool.getFormatText("viewall.error", "&cError: You must be a player to use this command.")));
                    return false;
                }
                if (!this.checkPermission("backpacks.admin.viewall", sender, true)) {
                    return false;
                }
                if (this.orderedBackpackMap == null || this.orderedBackpackMap.size() != this.plugin.getBackpackMap().size()) {
                    this.orderedBackpackMap = new TreeMap<Double, Backpack>();
                    for (final Backpack bp2 : this.plugin.getBackpackMap().values()) {
                        double d2 = bp2.getSlots();
                        for (boolean f2 = false; this.orderedBackpackMap.get(d2) != null && !f2; d2 += 0.001) {
                        }
                        this.orderedBackpackMap.put(d2, bp2);
                    }
                }
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex2) {
                    }
                }
                final Inventory inv2 = Bukkit.createInventory((InventoryHolder) new BackpackHolder(this.plugin, null).setViewMenu(true), 54, ColorTool.color(String.format(FormatTool.getFormatText("viewall.succuse", "Backpacks - &4Viewing All &8page %s"), page)));
                int counter2 = 0;
                for (final Backpack backpack : this.orderedBackpackMap.values()) {
                    if (counter2 >= (page - 1) * 54 && counter2 < page * 54) {
                        inv2.addItem(new ItemStack[]{backpack.getItem()});
                    }
                    if (++counter2 >= page * 54) {
                        break;
                    }
                }
                ((Player) sender).openInventory(inv2);
                Main.log.info("玩家<" + sender.getName() + "> 发起了查询所有背包的命令");
                return false;
            }
        } else {
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

    private boolean checkPermission(final String string, final CommandSender sender, final boolean sendMessage) {
        if (!sender.hasPermission(string)) {
            if (sendMessage) {
                sender.sendMessage(ColorTool.color(this.plugin.getPrefix() + String.format(FormatTool.getFormatText("permission.error", "&cYou do not have permission to use this command! (%s)"), string)));
                Main.log.info("User " + sender.getName() + " was denied access to a command. (" + string + ")");
            }
            return false;
        }
        return true;
    }

    private void removeBackPacks(final Backpack backpack) {
        final File file = new File(this.plugin.getDataFolder() + File.separator + "backpacks", backpack.getUniqueId() + ".yml");
        this.plugin.unregisterBackpack(backpack);
        this.plugin.getPluginPlayer(backpack.getBindID()).removeBackpack(backpack);
        file.delete();
    }
}
