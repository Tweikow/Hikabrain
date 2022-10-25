package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.utils.Manager;
import fr.tweikow.hikabrain.utils.StatsGame;
import org.bukkit.DyeColor;
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
        if (StatsGame.getStatus() == StatsGame.INGAME) {
            if (player.getLocation().getBlockY() <= Main.instance.getConfig().getDouble("hikabrain.dead_zone"))
                player.setHealth(0);
            if (block.getType() == Material.WOOL && Manager.isColoredWool(block, DyeColor.RED)) {
                player.sendMessage("Laine de couleur §cRouge");
            }
            if (block.getType() == Material.WOOL && Manager.isColoredWool(block, DyeColor.BLUE)) {
                player.sendMessage("Laine de couleur §9Bleu");
            }
        }
    }
}
