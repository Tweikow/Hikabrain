package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.utils.InvManager;
import fr.tweikow.hikabrain.utils.Manager;
import fr.tweikow.hikabrain.utils.StatsGame;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerManager implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage("");
        if (!Manager.waiting_players.contains(player.getUniqueId().toString())) {
            if (StatsGame.getStatus() == StatsGame.WAITING) {
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
            }
            if (StatsGame.getStatus() == StatsGame.WAITING)
                Manager.joinWaiting(player);
            if (StatsGame.getStatus() == StatsGame.INGAME)
                Manager.joinInGame(player);
            return;
        }
        if (!Manager.spectators.contains(player.getUniqueId().toString())) {
            Manager.spectators.add(player.getUniqueId().toString());
            player.setGameMode(GameMode.SPECTATOR);
        } else
            player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage("");

        Manager.quit(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        event.getEntity().getPlayer().spigot().respawn();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (StatsGame.getStatus() == StatsGame.INGAME) {
            player.getInventory().clear();
            if (Manager.team_red.contains(player.getUniqueId().toString())) {
                InvManager.sendStuff(player, "rouge");
                new BukkitRunnable() {
                    public void run() {
                        player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
                        cancel();
                    }
                }.runTaskTimer(Main.instance, 0, 1);
            }
            if (Manager.team_blue.contains(player.getUniqueId().toString())) {
                InvManager.sendStuff(player, "bleu");
                new BukkitRunnable() {
                    public void run() {
                        player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
                        cancel();
                    }
                }.runTaskTimer(Main.instance, 0, 1);
            }
        }
    }

    @EventHandler
    public void foodLevelChange(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }
}
