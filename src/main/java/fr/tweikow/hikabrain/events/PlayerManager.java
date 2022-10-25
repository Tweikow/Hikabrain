package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.utils.Manager;
import fr.tweikow.hikabrain.utils.StatsGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage("");

        if (StatsGame.getStatus() == StatsGame.WAITING) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
        }
        if (StatsGame.getStatus() == StatsGame.WAITING) {
            Manager.joinWaiting(player);
        } else if (!Manager.spectators.contains(player.getUniqueId().toString())) {
            Manager.spectators.add(player.getUniqueId().toString());
            player.setGameMode(GameMode.SPECTATOR);
        } else player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage("");

        Manager.quitWaiting(player);
    }
}
