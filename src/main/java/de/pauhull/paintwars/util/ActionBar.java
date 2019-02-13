package de.pauhull.paintwars.util;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar {

    public static void sendActionBar(Player player, String actionBar) {
        IChatBaseComponent actionBarComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + actionBar + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(actionBarComponent, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
