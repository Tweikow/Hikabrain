package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.board.Scoreboard;
import fr.tweikow.hikabrain.managers.GameManager;
import fr.tweikow.hikabrain.managers.PlayerManager;
import fr.tweikow.hikabrain.managers.SettingsManager;
import fr.tweikow.hikabrain.managers.StateGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Scoreboard.send(player);

        event.setJoinMessage("");
        if (!GameManager.waiting_players.contains(player.getUniqueId().toString())) {
            if (StateGame.getStatus() == StateGame.WAITING) {
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                PlayerManager.joinWaiting(player);
            }
            if (StateGame.getStatus() == StateGame.INGAME)
                PlayerManager.joinInGame(player);
            return;
        }
        if (!GameManager.spectators.contains(player.getUniqueId().toString())) {
            GameManager.spectators.add(player.getUniqueId().toString());
            player.setGameMode(GameMode.SPECTATOR);
        } else
            player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("");

        new PlayerManager().quit(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        player.spigot().respawn();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        new PlayerManager().respawn(event.getPlayer());
    }

    @EventHandler
    public void foodLevelChange(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void damagePlayer(EntityDamageByEntityEvent event) {
        if (StateGame.getStatus().equals(StateGame.WAITING) || StateGame.getStatus().equals(StateGame.STARTING))
            event.setCancelled(true);
    }
}
