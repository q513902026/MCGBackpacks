package me.renner6895.backpacks;

import org.bukkit.configuration.file.*;
import org.bukkit.inventory.*;
import org.bukkit.configuration.*;

import java.util.*;

public class InvUtil {
    public static String saveInventory(final Inventory inventory) {
        final YamlConfiguration config = new YamlConfiguration();
        saveInventory(inventory, (ConfigurationSection) config, inventory.getSize());
        return config.saveToString();
    }

    public static void saveInventory(final Inventory inventory, final ConfigurationSection destination, final int slotsToSave) {
        for (int i = 0; i < inventory.getSize() && i + 1 <= slotsToSave; ++i) {
            final ItemStack item = inventory.getItem(i);
            destination.set(Integer.toString(i), (Object) item);
        }
    }

    public static ItemStack[] loadInventory(final String data) throws InvalidConfigurationException {
        final YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(data);
        return loadInventory((ConfigurationSection) config);
    }

    public static ItemStack[] loadInventory(final ConfigurationSection source) throws InvalidConfigurationException {
        final ArrayList stacks = new ArrayList();
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
                stacks.set(number, source.get(key));
            }
        } catch (NumberFormatException var4) {
            throw new InvalidConfigurationException("Expected a number.", (Throwable) var4);
        }
        return (ItemStack[]) stacks.toArray(new ItemStack[0]);
    }
}
