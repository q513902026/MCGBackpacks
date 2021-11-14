package me.renner6895.backpacks.commands;

import com.google.common.collect.Lists;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.objects.BackpackHolder;
import me.renner6895.backpacks.objects.PluginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

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
            if (args[0].equalsIgnoreCase("create")) {
                if (!this.checkPermission("backpacks.admin.create", sender, true)) {
                    return false;
                }
                this.createBackpack((Player) sender, args);
                return false;
            } else if (args[0].equalsIgnoreCase("give")) {
                if (!this.checkPermission("backpacks.admin.give", sender, true)) {
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("give.error", "&cError: For information on how to use this command, type /backpacks help give")));
                    return false;
                }
                final Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + String.format(this.getFormatText("give.error2", "&7The player %s is not online"), args[1])));
                    return false;
                }
                this.createBackpack(player, args);
                sender.sendMessage(this.color(this.plugin.getPrefix() + String.format(this.getFormatText("give.succuse", "&7New Backpack given to %s ."), args[1])));
                return false;
            } else if (args[0].equalsIgnoreCase("clone")) {
                if (!this.checkPermission("backpacks.edit.clone", sender, true)) {
                    return false;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("clone.error", "&cYou can only use this command as a player!")));
                    return false;
                }
                final Player player = (Player) sender;
                if (this.plugin.itemIsBackpack(player.getInventory().getItemInMainHand())) {
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() + 1);
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("clone.succuse", "&7The backpack item has been cloned!")));
                } else {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("clone.error2", "&cYou must be holding a backpack for this to work!")));
                }
                return false;
            } else if (args[0].equalsIgnoreCase("rename")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("rename.error", "&cError: You must be a player to use this command.")));
                    return false;
                }
                if (!this.checkPermission("backpacks.edit.rename", sender, true)) {
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("rename.error2", "&cError: For information on how to use this command, type /backpacks help rename")));
                    return false;
                }
                final Player player = (Player) sender;
                final ItemStack item = player.getInventory().getItemInMainHand();
                if (!this.plugin.itemIsBackpack(item)) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("rename.error3", "&cError: You must be holding the backpack in your hand to rename it.")));
                    return false;
                }
                final String name = args[1].replaceAll("_", " ");
                final String backpackId = this.plugin.getNmsUtil().getStringTag(item, "backpack-item");
                final Backpack backpack = this.plugin.getBackpack(UUID.fromString(backpackId));
                backpack.updateName(name);
                final ItemStack newBPItem = backpack.getItem();
                newBPItem.setAmount(item.getAmount());
                player.getInventory().setItemInMainHand(newBPItem);
                sender.sendMessage(this.color(this.plugin.getPrefix() + String.format(this.getFormatText("rename.succuse", "&7Backpack renamed to %s &7."), name)));
                return false;
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("delete.error", "&cYou can only use this command as a player!")));
                    return false;
                }
                if (!this.checkPermission("backpacks.edit.delete", sender, true)) {
                    return false;
                }
                final Player player = (Player) sender;
                final ItemStack item = player.getInventory().getItemInMainHand();
                if (this.plugin.itemIsBackpack(item)) {
                    final String backpackId2 = this.plugin.getNmsUtil().getStringTag(item, "backpack-item");
                    final Backpack backpack2 = this.plugin.getBackpack(UUID.fromString(backpackId2));
                    backpack2.removeBackpack();
                    item.setAmount(0);
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("delete.succuse", "&7The backpack has been delete!")));
                } else {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("delete.error2", "&cYou must be holding a backpack for this to work!")));
                }
                return false;
            } else if (args[0].equalsIgnoreCase("reslot")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("reslot.error", "&cError: You must be a player to use this command.")));
                    return false;
                }
                if (!this.checkPermission("backpacks.edit.reslot", sender, true)) {
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("reslot.error2", "&cError: For information on how to use this command, type /backpacks help reslot")));
                    return false;
                }
                int page;
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException var18) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("reslot.error3", "&cError: For information on how to use this command, type /backpacks help reslot")));
                    return false;
                }
                if (page < 1 || page > 54) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("reslot.error3", "&cError: For information on how to use this command, type /backpacks help reslot")));
                    return false;
                }
                final Player player2 = (Player) sender;
                final ItemStack item2 = player2.getInventory().getItemInMainHand();
                if (!this.plugin.itemIsBackpack(item2)) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("reslot.error4", "&cError: You must be holding the backpack in your hand to reslot it.")));
                    return false;
                }
                final String backpackId = ((MemorySection) this.plugin.getNmsUtil().getTag(item2)).getString("backpack-item");
                final Backpack backpack = this.plugin.getBackpack(UUID.fromString(backpackId));
                backpack.updateSlots(page);
                final ItemStack newBPItem = backpack.getItem();
                newBPItem.setAmount(item2.getAmount());
                player2.getInventory().setItemInMainHand(newBPItem);
                sender.sendMessage(this.color(this.plugin.getPrefix() + String.format(this.getFormatText("reslot.succuse", "&7Backpack reslotted to  %s slots&7."), page)));
                return false;
            } else if (args[0].equalsIgnoreCase("find")) {
                if (!this.checkPermission("backpacks.admin.find", sender, true)) {
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("find.error", "&cError: You must be a player name use this command.")));
                    return false;
                }
                final String bindID = args[1];
                final List<Backpack> backpacks = this.plugin.getPluginPlayer(bindID) ==null ? findOfflinePlayerBackpacks(bindID) : this.plugin.getPluginPlayer(bindID).getBackpacks();
                if(backpacks.size() == 0 ){
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("find.error2", "&cError: You must be a vaild player name use this command.")));
                    return false;
                }
                final List<String> list = Lists.newArrayList();
                for (final Backpack backpack : backpacks) {
                    list.add("Name:{" + backpack.getName() + "} | UUID:{" + backpack.getUniqueId().toString() + "} | Slot:{" + backpack.getSlots() + "} ");
                }
                sender.sendMessage(this.color(String.format("&3&m-----------------------%s&3&m---------------------------",bindID)));
                for (final String message : list) {
                    sender.sendMessage(message);
                }
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                return false;
            } else if (args[0].equalsIgnoreCase("view")) {
                if (!this.checkPermission("backpacks.admin.view", sender, true)) {
                    return false;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("view.error", "&cError: You must be a player to use this command.")));
                    return false;
                }
                if (args.length < 2) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("view.error", "&cError: You must be a player name use this command.")));
                    return false;
                }
                final String bindID = args[1];
                if (this.plugin.getPluginPlayer(bindID) ==null){
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("view.error3", "&cError: player must be online.")));
                    return false;
                }
                PluginPlayer pluginPlayer = this.plugin.getPluginPlayer(bindID);
                final List<Backpack> backpacks =  pluginPlayer.getBackpacks();
                if(backpacks.size() == 0 ){
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("view.error2", "&cError: You must be a vaild player name use this command.")));
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
                final Inventory inv = pluginPlayer.getBackpackListInv(Bukkit.createInventory((InventoryHolder) new BackpackHolder(this.plugin, null).setViewMenu(true), 54, this.color(String.format(this.getFormatText("viewall.succuse", "Backpacks - &4Viewing %s &8page %s"), bindID, page2))),page2);
                ((Player) sender).openInventory(inv);
                Main.INSTANCE().log("玩家<"+sender.getName()+"> 正在查询玩家<"+bindID+">的所有背包");
                return false;
            }else if (args[0].equalsIgnoreCase("rebuildCache")){
                if (!this.checkPermission("backpacks.admin.view", sender, true)) {
                    return false;
                }
                if (sender instanceof ConsoleCommandSender){
                    this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin,() ->Main.INSTANCE().buildCache());
                    this.plugin.log("正在异步建立缓存...");
                }
                return false;
            } else {
                if (!args[0].equalsIgnoreCase("viewall")) {
                    return false;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("viewall.error", "&cError: You must be a player to use this command.")));
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
                final Inventory inv2 = Bukkit.createInventory((InventoryHolder) new BackpackHolder(this.plugin, null).setViewMenu(true), 54, this.color(String.format(this.getFormatText("viewall.succuse", "Backpacks - &4Viewing All &8page %s"), page)));
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
                Main.INSTANCE().log("玩家<"+sender.getName()+"> 发起了查询所有背包的命令");
                return false;
            }
        } else {
            if (args.length <= 1) {
                final List<String> list2 = new ArrayList<String>();
                list2.add(this.color("&3&m----------------------&3[&bBackpacks&3]&m---------------------"));
                list2.add(this.color("&3This plugin is maintained and developed by TheRealJeremy."));
                list2.add(this.color("&3Modified By HopeAsd , Color_yr."));
                if (sender.hasPermission("backpacks.edit.rename")) {
                    list2.add(this.color("&3/backpack rename {name}"));
                    list2.add(this.color("&7/backpack help rename"));
                }
                if (sender.hasPermission("backpacks.edit.reslot")) {
                    list2.add(this.color("&3/backpack reslot {int}"));
                    list2.add(this.color("&7/backpack help reslot"));
                }
                if (sender.hasPermission("backpacks.edit.clone")) {
                    list2.add(this.color("&3/backpack clone"));
                    list2.add(this.color("&7/backpack help clone"));
                }
                if (sender.hasPermission("backpacks.admin.create")) {
                    list2.add(this.color("&3/backpack create <slots> <name> <item>"));
                    list2.add(this.color("&7/backpack help create"));
                }
                if (sender.hasPermission("backpacks.admin.give")) {
                    list2.add(this.color("&3/backpack give {player} <slots> <name> <item>"));
                    list2.add(this.color("&7/backpack help give"));
                }
                if (sender.hasPermission("backpacks.admin.viewall")) {
                    list2.add(this.color("&3/backpack viewall <page>"));
                    list2.add(this.color("&7/backpack help viewall"));
                }
                if (sender.hasPermission("backpacks.admin.find")) {
                    list2.add(this.color("&3/backpack find {player}"));
                    list2.add(this.color("&7/backpack help find"));
                }
                if (sender.hasPermission("backpacks.admin.view")) {
                    list2.add(this.color("&3/backpack view {player} <page>"));
                    list2.add(this.color("&7/backpack help view"));
                }
                if (sender instanceof ConsoleCommandSender){
                    list2.add(this.color("&3/backpack rebuildCache"));
                }
                list2.add(this.color("&3&m-----------------------------------------------------"));
                for (final String s : list2) {
                    sender.sendMessage(s);
                }
                return false;
            }
            if (args[1].equalsIgnoreCase("rename")) {
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(this.color("&3/backpacks rename {name}"));
                sender.sendMessage(this.color("&7- Rename a backpack you are holding in your hand!"));
                sender.sendMessage(this.color("&7- To use spaces in the name, use an underscore, Ex: \"My_Backpack_Name\"."));
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("reslot")) {
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(this.color("&3/backpacks reslot {int}"));
                sender.sendMessage(this.color("&7- Change the slots of a backpack you are holding in your hand!"));
                sender.sendMessage(this.color("&7- The slot number must be an integer from 1 to 54."));
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("clone")) {
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(this.color("&3/backpacks clone"));
                sender.sendMessage(this.color("&7- Creates an extra copy of the backpack!"));
                sender.sendMessage(this.color("&7- Useful for giving to friends, but careful who you trust!"));
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("create")) {
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(this.color("&3/backpack create <slots> <name> <item>"));
                sender.sendMessage(this.color("&7- Create a backpack for yourself!"));
                sender.sendMessage(this.color("&7- The slots, name, and item are all optional, and will default if not given."));
                sender.sendMessage(this.color("&7- To use the slots option inclued \"slots:{int}\" in your command."));
                sender.sendMessage(this.color("&7- To use the name option inclued \"name:{name}\" in your command."));
                sender.sendMessage(this.color("&7- To use the item option inclued \"item:{item-id}:{item-data}\" in your command."));
                sender.sendMessage(this.color("&7- Ex: \"/bp create slots:14 name:&My_Backpack_Name item:35:9\"."));
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("give")) {
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(this.color("&3/backpack give {player} <slots> <name> <item>"));
                sender.sendMessage(this.color("&7- Give a backpack to someone!"));
                sender.sendMessage(this.color("&7- The {player} argument is required for this command!"));
                sender.sendMessage(this.color("&7- The slots, name, and item are all optional, and will default if not given."));
                sender.sendMessage(this.color("&7- To use the slots option inclued \"slots:{int}\" in your command."));
                sender.sendMessage(this.color("&7- To use the name option inclued \"name:{name}\" in your command."));
                sender.sendMessage(this.color("&7- To use the item option inclued \"item:{item-id}:{item-data}\" in your command."));
                sender.sendMessage(this.color("&7- Ex: \"/bp give TheRealJeremy slots:14 name:&My_Backpack_Name item:35:9\"."));
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("find")) {
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(this.color("&3/backpack find {player}"));
                sender.sendMessage(this.color("&7- find player backpacks List!"));
                sender.sendMessage(this.color("&7- The {player} argument is required for this command!"));
                sender.sendMessage(this.color("&7- Ex: \"/bp find TheRealJeremy \"."));
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("view")) {
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(this.color("&3/backpacks view {player} <page>"));
                sender.sendMessage(this.color("&7- View player the backpacks on the server!"));
                sender.sendMessage(this.color("&7- The {player} argument is required for this command!"));
                sender.sendMessage(this.color("&7- Click on a backpack to receive a copy of it."));
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                return false;
            }
            if (args[1].equalsIgnoreCase("viewall")) {
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                sender.sendMessage(this.color("&3/backpacks viewall <page>"));
                sender.sendMessage(this.color("&7- View all the backpacks on the server!"));
                sender.sendMessage(this.color("&7- Click on a backpack to receive a copy of it."));
                sender.sendMessage(this.color("&3&m-----------------------------------------------------"));
                return false;
            }
            return false;
        }
    }

    private List<Backpack> findOfflinePlayerBackpacks(String bindID) {
        List<Backpack> backpacks = Lists.newArrayList();
        for(Backpack backpack:plugin.getBackpackMap().values()){
            if(backpack.getBindID().equals(bindID)){
                backpacks.add(backpack);
            }
        }
        return backpacks;
    }

    private boolean checkPermission(final String string, final CommandSender sender, final boolean sendMessage) {
        if (!sender.hasPermission(string)) {
            if (sendMessage) {
                sender.sendMessage(this.color(this.plugin.getPrefix() + String.format(this.getFormatText("permission.error", "&cYou do not have permission to use this command! (%s)"), string)));
                this.plugin.log("User " + sender.getName() + " was denied access to a command. (" + string + ")");
            }
            return false;
        }
        return true;
    }

    private String color(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private void createBackpack(final Player player, final String[] args) {
        int slots = Main.defaultSlots;
        String name = ChatColor.translateAlternateColorCodes('&', Main.defaultName);
        int itemId = Main.defaultItemId;
        short itemData = Main.defaultItemData;
        String id = player.getName();
        final String[] var10 = args;
        for (int var11 = args.length, var12 = 0; var12 < var11; ++var12) {
            final String s = var10[var12];
            if (s.toLowerCase().startsWith("slots:")) {
                try {
                    slots = Integer.parseInt(s.substring("slots:".length()));
                } catch (NumberFormatException var14) {
                    slots = Main.defaultSlots;
                }
                if (slots < 1 || slots > 54) {
                    slots = Main.defaultSlots;
                }
            } else if (s.toLowerCase().startsWith("name:")) {
                name = ChatColor.translateAlternateColorCodes('&', s.substring("name:".length()).replace("_", " "));
            } else if (s.toLowerCase().startsWith("item:")) {
                final String item = s.substring("item:".length());
                try {
                    itemId = Integer.parseInt(item.split(":")[0]);
                } catch (NumberFormatException ex) {
                }
                try {
                    itemData = (short) Integer.parseInt(item.split(":")[1]);
                } catch (ArrayIndexOutOfBoundsException ex2) {
                } catch (NumberFormatException ex3) {
                }
            } else if (s.toLowerCase().startsWith("bind:")) {
                final String bindName = s.substring("bind:".length());
                final Player bindPlayer = Bukkit.getPlayer(bindName);
                if (bindPlayer != null) {
                    id = bindPlayer.getName();
                }
            }
        }
        final UUID randomId = UUID.randomUUID();
        final File file = new File(this.plugin.getDataFolder() + File.separator + "backpacks", randomId + ".yml");
        final FileConfiguration fileConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
        fileConfig.set("slots", (Object) slots);
        fileConfig.set("name", (Object) name);
        fileConfig.set("item-id", (Object) itemId);
        fileConfig.set("item-data", (Object) itemData);
        fileConfig.set("bind-id", (Object) id);
        try {
            fileConfig.save(file);
        } catch (IOException var13) {
            var13.printStackTrace();
        }
        final Backpack backpack = new Backpack(file, this.plugin);
        backpack.load();
        final PluginPlayer pluginPlayer = this.plugin.getPluginPlayer(id);
        this.plugin.registerBackpack(backpack);
        pluginPlayer.addBackpack(backpack);
        player.getInventory().addItem(new ItemStack[]{backpack.getItem()});
        player.sendMessage(this.color(this.plugin.getPrefix() + this.getFormatText("backpack.give", "&7You were given a new Backpack!")));
    }

    private void removeBackPacks(final Backpack backpack) {
        final File file = new File(this.plugin.getDataFolder() + File.separator + "backpacks", backpack.getUniqueId() + ".yml");
        this.plugin.unregisterBackpack(backpack);
        this.plugin.getPluginPlayer(backpack.getBindID()).removeBackpack(backpack);
        file.delete();
    }
}
