package de.pauhull.paintwars.util;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.concurrent.TimeUnit;

public class HeadCache {

    private TimedHashMap<String, net.minecraft.server.v1_12_R1.ItemStack> heads;

    public HeadCache() {
        this(TimeUnit.MINUTES, 30);
    }

    public HeadCache(TimeUnit unit, long expireAfter) {
        this.heads = new TimedHashMap<>(unit, expireAfter);
    }

    public ItemStack getHead(String owner) {
        if (heads.containsKey(owner)) {
            return CraftItemStack.asBukkitCopy(heads.get(owner));
        } else {
            saveHead(owner);
            return getHead(owner);
        }
    }

    public void saveHead(String owner) {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(owner);
        stack.setItemMeta(meta);
        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        heads.put(owner, nmsStack);
    }

}
