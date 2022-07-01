package me.renner6895.backpacks.commands;

import me.hope.core.inject.annotation.command.CommandPermission;
import me.renner6895.backpacks.Main;
import me.renner6895.backpacks.commands.abstractclass.HopeCommand;
import me.renner6895.backpacks.objects.Backpack;
import me.renner6895.backpacks.objects.PluginPlayer;
import me.renner6895.backpacks.tools.ColorTool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author xiaoyv_404
 * @create 2022/6/19
 **/
@CommandPermission("backpacks.admin.create")
public class CreateCommand extends HopeCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        createBackpack((Player) commandSender, args);
        return true;
    }

    public static void createBackpack(Player player, final String[] args) {
        int slots = Main.defaultSlots;
        String name = ChatColor.translateAlternateColorCodes('&', Main.defaultName);
        int itemId = Main.defaultItemId;
        short itemData = Main.defaultItemData;
        String id = player.getName();
        for (int var11 = args.length, var12 = 0; var12 < var11; ++var12) {
            final String s = args[var12];
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
                } catch (NumberFormatException ignored) {
                }
                try {
                    itemData = (short) Integer.parseInt(item.split(":")[1]);
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {

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
        final File file = new File(getPlugin().getDataFolder() + File.separator + "backpacks", randomId + ".yml");
        final FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
        fileConfig.set("slots", slots);
        fileConfig.set("name", name);
        fileConfig.set("item-id", itemId);
        fileConfig.set("item-data", itemData);
        fileConfig.set("bind-id", id);
        try {
            fileConfig.save(file);
        } catch (IOException var13) {
            var13.printStackTrace();
        }
        final Backpack backpack = new Backpack(file);
        backpack.load();
        final PluginPlayer pluginPlayer = getPlugin().getPluginPlayer(id);
        getPlugin().registerBackpack(backpack);
        pluginPlayer.addBackpack(backpack);
        player.getInventory().addItem(backpack.getItem());
        player.sendMessage(ColorTool.color(getPlugin().getPrefix() + getFormatText()));
    }

    private static String getFormatText() {
        return getPlugin().getConfig().getString("lang.backpack.give", "&7You were given a new Backpack!");
    }

}
