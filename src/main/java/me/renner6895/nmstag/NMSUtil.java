package me.renner6895.nmstag;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.inventory.*;

public interface NMSUtil {
    boolean hasKey(final ItemStack p0, final String p1);

    Object getTag(final ItemStack p0);
    Object getTag(final ItemStack p0,final String key);

    String getStringTag(final ItemStack p0, final String p1);

    int getIntTag(final ItemStack p0, final String p1);

    long getLongTag(final ItemStack p0, final String p1);

    boolean getBooleanTag(final ItemStack p0, final String p1);

    ItemStack setTag(final ItemStack p0,final String key, final NBTTagCompound tag);
    ItemStack setStringTag(final ItemStack p0, final String p1, final String p2);

    ItemStack setIntTag(final ItemStack p0, final String p1, final int p2);

    ItemStack setLongTag(final ItemStack p0, final String p1, final long p2);

    ItemStack setBooleanTag(final ItemStack p0, final String p1, final boolean p2);
}
