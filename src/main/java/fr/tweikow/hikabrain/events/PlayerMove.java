package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.utils.Manager;
import fr.tweikow.hikabrain.utils.StatsGame;
import org.bukkit.Bukkit;
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

        if (StatsGame.getStatus() == StatsGame.FINISH) {
            if (player.getLocation().getBlockY() <= Main.instance.getConfig().getDouble("hikabrain.dead_zone")) {
                if (Manager.team_red.contains(player.getUniqueId().toString()))
                   player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
                if (Manager.team_blue.contains(player.getUniqueId().toString()))
                    player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
            }
        }if (StatsGame.getStatus() == StatsGame.INGAME) {
            if (player.getLocation().getBlockY() <= Main.instance.getConfig().getDouble("hikabrain.dead_zone"))
                player.setHealth(0);
            if (block.getType() == Material.WOOL && (Manager.isColoredWool(block, DyeColor.RED) || Manager.isColoredWool(block, DyeColor.BLUE))) {
                Manager.teamTeleport();
                if (block.getType() == Material.WOOL && Manager.isColoredWool(block, DyeColor.RED))
                    Manager.addScore("bleu");
                if (block.getType() == Material.WOOL && Manager.isColoredWool(block, DyeColor.BLUE))
                    Manager.addScore("rouge");
            }
        }
    }
}
