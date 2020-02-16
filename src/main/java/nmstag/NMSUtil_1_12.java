package nmstag;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.*;


public class NMSUtil_1_12 implements NMSUtil {
    @Override
    public boolean hasKey(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().hasKey(str);
    }

    @Override
    public Object getTag(final ItemStack item) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return (nmsItem == null) ? null : nmsItem.getTag();
    }

    @Override
    public String getStringTag(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getString(str);
    }

    @Override
    public int getIntTag(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getInt(str);
    }

    @Override
    public long getLongTag(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getLong(str);
    }

    @Override
    public boolean getBooleanTag(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getBoolean(str);
    }

    @Override
    public ItemStack setStringTag(final ItemStack item, final String str1, final String str2) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        final NBTTagCompound tag = nmsItem.getTag();
        tag.setString(str1, str2);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public ItemStack setIntTag(final ItemStack item, final String str1, final int n) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        final NBTTagCompound tag = nmsItem.getTag();
        tag.setInt(str1, n);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public ItemStack setLongTag(final ItemStack item, final String str1, final long l) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        final NBTTagCompound tag = nmsItem.getTag();
        tag.setLong(str1, l);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public ItemStack setBooleanTag(final ItemStack item, final String str1, final boolean b) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        final NBTTagCompound tag = nmsItem.getTag();
        tag.setBoolean(str1, b);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }
}
