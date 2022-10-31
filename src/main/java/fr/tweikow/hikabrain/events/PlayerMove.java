package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.utils.Manager;
import fr.tweikow.hikabrain.utils.StatsGame;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

        if (StatsGame.getStatus() == StatsGame.LAUNCHING)
            if (Manager.team_red.contains(player.getUniqueId().toString()) || Manager.team_blue.contains(player.getUniqueId().toString()))
                event.setCancelled(true);

        if (StatsGame.getStatus() == StatsGame.FINISH)
            if (player.getLocation().getBlockY() <= Main.instance.getConfig().getDouble("hikabrain.dead_zone"))
                Manager.playerTeleport(player);
        if (StatsGame.getStatus() == StatsGame.INGAME) {
            if (Manager.respawn.contains(player.getUniqueId().toString()))
                event.setCancelled(true);
            if (player.getLocation().getBlockY() <= Main.instance.getConfig().getDouble("hikabrain.dead_zone"))
                player.setHealth(0);
            if (block.getType() == Material.RED_WOOL && Manager.team_blue.contains(player.getUniqueId().toString()))
                Manager.addScore("bleu");
            if (block.getType() == Material.BLUE_WOOL && Manager.team_red.contains(player.getUniqueId().toString()))
                Manager.addScore("rouge");
        }
    }
}
