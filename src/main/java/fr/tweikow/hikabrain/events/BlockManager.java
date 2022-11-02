package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.managers.GameManager;
import fr.tweikow.hikabrain.managers.SettingsManager;
import fr.tweikow.hikabrain.managers.StateGame;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockManager implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockAgainst().getType().equals(Material.BARRIER)) {
            event.setCancelled(true);
            return;
        }
        if (StateGame.getStatus().equals(StateGame.FINISH))
            event.setCancelled(true);
        if (StateGame.getStatus().equals(StateGame.WAITING)) {
            if (!GameManager.waiting_players.contains(event.getPlayer().getUniqueId().toString()))
                return;
            event.setCancelled(true);
        }
        if (StateGame.getStatus().equals(StateGame.INGAME)) {
            if (SettingsManager.coordonates.contains(event.getBlockPlaced().getLocation()))
                event.setCancelled(true);
            if (!GameManager.breaks.contains(event.getBlock().getLocation()))
                GameManager.places.add(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (StateGame.getStatus().equals(StateGame.FINISH))
            event.setCancelled(true);
        if (StateGame.getStatus().equals(StateGame.WAITING)) {
            if (!GameManager.waiting_players.contains(event.getPlayer().getUniqueId().toString()))
                return;
            event.setCancelled(true);
        }
        if (StateGame.getStatus().equals(StateGame.INGAME)) {
            if (SettingsManager.coordonates.contains(event.getBlock().getLocation()))
                event.setCancelled(true);
            GameManager.breaks.add(event.getBlock().getLocation());
        }
    }
}
