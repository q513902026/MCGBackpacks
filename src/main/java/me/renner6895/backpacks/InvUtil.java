package me.renner6895.backpacks;

import com.google.common.collect.Lists;
import me.hope.core.inject.annotation.Inject;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class InvUtil {
    @Inject
    private static Main plugin;
    public static String saveInventory(final Inventory inventory) {
        final YamlConfiguration config = new YamlConfiguration();
        saveInventory(inventory, config, inventory.getSize());
        return config.saveToString();
    }

    private static String getNBTString(ItemStack item) {
        if (item != null) {
            NBTTagCompound nbt = (NBTTagCompound) plugin.getNmsUtil().getTag(item);
            if (nbt != null &&nbt.hasKey("BlockEntityTag")) {
                nbt = nbt.getCompound("BlockEntityTag");
                try {
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    NBTCompressedStreamTools.a(nbt, buf);
                    return Base64.encodeBase64String(buf.toByteArray());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    private static ItemStack setNBTString(String nbt, ItemStack item) {
        if (item != null && nbt != null) {
            try {
                ByteArrayInputStream buf = new ByteArrayInputStream(Base64.decodeBase64(nbt));
                NBTTagCompound nbtc = NBTCompressedStreamTools.a(buf);
                item = plugin.getNmsUtil().setTag(item, "BlockEntityTag",nbtc);
                return item;
            } catch (IOException ex) {

            }
        }
        return item;
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
                ItemStack item = source.getItemStack(key);
                /**
                String nbtstr = source.getString(key+"BlockEntityTag");
                item = setNBTString(nbtstr, item);
                **/
                stacks.set(number, item);
            }
        } catch (NumberFormatException var4) {
            throw new InvalidConfigurationException("Expected a number.", var4);
        }
        return stacks.toArray(new ItemStack[0]);
    }
}
