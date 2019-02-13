package de.pauhull.paintwars.util;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {

    public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        IChatBaseComponent titleComponent = null;
        IChatBaseComponent subTitleComponent = null;

        if (title != null) {
            titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}");
        }

        if (subTitle != null) {
            subTitleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subTitle + "\"}");
        }

        if (titleComponent != null && subTitleComponent != null) {
            PacketPlayOutTitle timings = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
            connection.sendPacket(timings);
        }

        if (titleComponent != null) {
            PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComponent);
            connection.sendPacket(titlePacket);
        }

        if (subTitleComponent != null) {
            PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subTitleComponent);
            connection.sendPacket(subTitlePacket);
        }
    }

}
