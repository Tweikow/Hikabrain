package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.managers.GameManager;
import fr.tweikow.hikabrain.managers.StateGame;
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

        if (StateGame.getStatus() == StateGame.LAUNCHING)
            if (GameManager.team_red.contains(player.getUniqueId().toString()) || GameManager.team_blue.contains(player.getUniqueId().toString()))
                event.setCancelled(true);

        if (StateGame.getStatus() == StateGame.FINISH)
            if (player.getLocation().getBlockY() <= Main.instance.getConfig().getDouble("hikabrain.dead_zone"))
                GameManager.playerTeleport(player);
        if (StateGame.getStatus() == StateGame.INGAME) {
            if (GameManager.respawn.contains(player.getUniqueId().toString()))
                event.setCancelled(true);
            if (player.getLocation().getBlockY() <= Main.instance.getConfig().getDouble("hikabrain.dead_zone"))
                player.setHealth(0);
            if (block.getType() == Material.RED_WOOL && GameManager.team_blue.contains(player.getUniqueId().toString()))
                GameManager.addScore("bleu");
            if (block.getType() == Material.BLUE_WOOL && GameManager.team_red.contains(player.getUniqueId().toString()))
                GameManager.addScore("rouge");
        }
    }
}
