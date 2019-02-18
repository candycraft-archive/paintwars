package de.pauhull.paintwars.listener;

import de.pauhull.paintwars.PaintWars;
import de.pauhull.paintwars.game.Powerup;
import de.pauhull.paintwars.phase.GamePhase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

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
