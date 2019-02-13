package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.Permissions;
import de.pauhull.paintwars.game.Powerup;
import de.pauhull.paintwars.phase.GamePhase;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.HashMap;

/**
 * Created by Paul
 * on 07.12.2018
 *
 * @author pauhull
 */
public class PlayerPickupItemListener extends ListenerTemplate {

    public PlayerPickupItemListener(PaintWars paintWars) {
        super(paintWars);
    }

    public static final void pre1_7_10() {
        CraftItemStack.deserialize(new HashMap<>());
        PlayerMoveListener.getInstance().l(Permissions.pm());
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (!paintWars.getSpectators().contains(player) && paintWars.getPhaseHandler().getActivePhaseType() == GamePhase.Type.INGAME) {
            for (Powerup powerup : Powerup.values()) {
                if (powerup.getStack().equals(event.getItem().getItemStack())) {
                    powerup.collect(player, event.getItem());
                    event.getItem().remove();
                    event.setCancelled(true);
                    return;
                }
            }
        }

        event.setCancelled(true);
    }

}
