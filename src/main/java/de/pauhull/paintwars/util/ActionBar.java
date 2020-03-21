package de.pauhull.paintwars.util;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar {

    public static void sendActionBar(Player player, String actionBar) {
        IChatBaseComponent actionBarComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + actionBar + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(actionBarComponent, ChatMessageType.GAME_INFO);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
