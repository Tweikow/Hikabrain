package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.utils.Manager;
import fr.tweikow.hikabrain.utils.StatsGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockManager implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (StatsGame.getStatus().equals(StatsGame.FINISH))
            event.setCancelled(true);
        if (StatsGame.getStatus().equals(StatsGame.WAITING)) {
            if (!Manager.waiting_players.contains(event.getPlayer().getUniqueId().toString()))
                return;
            event.setCancelled(true);
        }
        if (StatsGame.getStatus().equals(StatsGame.INGAME)) {
            if (!Manager.blocks.contains(event.getBlockPlaced().getLocation()))
                Manager.blocks.add(event.getBlockPlaced().getLocation());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        if (StatsGame.getStatus().equals(StatsGame.FINISH))
            event.setCancelled(true);
        if (StatsGame.getStatus().equals(StatsGame.WAITING)) {
            if (!Manager.waiting_players.contains(event.getPlayer().getUniqueId().toString()))
                return;
            event.setCancelled(true);
        }
        if (StatsGame.getStatus().equals(StatsGame.INGAME)) {
            if (Manager.blocks.contains(event.getBlock().getLocation()))
                Manager.blocks.remove(event.getBlock().getLocation());
            else
                event.setCancelled(true);
        }
    }
}
