package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.managers.GameManager;
import fr.tweikow.hikabrain.managers.StateGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockManager implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (StateGame.getStatus().equals(StateGame.FINISH))
            event.setCancelled(true);
        if (StateGame.getStatus().equals(StateGame.WAITING)) {
            if (!GameManager.waiting_players.contains(event.getPlayer().getUniqueId().toString()))
                return;
            event.setCancelled(true);
        }
        if (StateGame.getStatus().equals(StateGame.INGAME)) {
            if (!GameManager.blocks.contains(event.getBlockPlaced().getLocation()))
                GameManager.blocks.add(event.getBlockPlaced().getLocation());
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
            if (GameManager.blocks.contains(event.getBlock().getLocation()))
                GameManager.blocks.remove(event.getBlock().getLocation());
            else
                event.setCancelled(true);
        }
    }
}
