package de.pauhull.paintwars.util;

import org.bukkit.entity.Player;

public class Title {

    @Deprecated
    public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
    }

}
