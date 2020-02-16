package me.renner6895.backpacks;

import com.google.common.collect.Lists;
import org.bukkit.configuration.file.*;
import org.bukkit.inventory.*;
import org.bukkit.configuration.*;

import java.util.*;

public class InvUtil {
    public static String saveInventory(final Inventory inventory) {
        final YamlConfiguration config = new YamlConfiguration();
        saveInventory(inventory, config, inventory.getSize());
        return config.saveToString();
    }

    public static void saveInventory(final Inventory inventory, final ConfigurationSection destination, final int slotsToSave) {
        for (int i = 0; i < inventory.getSize() && i + 1 <= slotsToSave; ++i) {
            final ItemStack item = inventory.getItem(i);
            destination.set(Integer.toString(i), item);
        }
    }

    public static ItemStack[] loadInventory(final String data) throws InvalidConfigurationException {
        final YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(data);
        return loadInventory(config);
    }

    public static ItemStack[] loadInventory(final ConfigurationSection source) throws InvalidConfigurationException {
        final List<ItemStack> stacks = Lists.newArrayList();
        try {
            for (final String key : source.getKeys(false)) {
                try {
                    Integer.parseInt(key);
                } catch (NumberFormatException var5) {
                    continue;
                }
                final int number = Integer.parseInt(key);
                while (stacks.size() <= number) {
                    stacks.add(null);
                }
                stacks.set(number, source.getItemStack(key));
            }
        } catch (NumberFormatException var4) {
            throw new InvalidConfigurationException("Expected a number.", var4);
        }
        return stacks.toArray(new ItemStack[0]);
    }
}
