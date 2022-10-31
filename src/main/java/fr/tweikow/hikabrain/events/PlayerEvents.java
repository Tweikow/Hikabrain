package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.board.FastBoard;
import fr.tweikow.hikabrain.board.Scoreboard;
import fr.tweikow.hikabrain.managers.GameManager;
import fr.tweikow.hikabrain.managers.StateGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
                GameManager.joinWaiting(player);
            }
            if (StateGame.getStatus() == StateGame.INGAME)
                GameManager.joinInGame(player);
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

        FastBoard board = Scoreboard.boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }

        GameManager.quit(player);
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
        if (StateGame.getStatus() == StateGame.INGAME) {
            GameManager.respawn.add(player.getUniqueId().toString());
            player.getInventory().clear();
            GameManager.playerTeleport(player);

            new BukkitRunnable() {
                int i = 5;
                public void run() {
                    if (i != 0) {
                        i--;
                        player.sendTitle("§6§lPrêt à reprendre ?", "§e" + i + " secondes", 15, 30, 15);
                    }
                    else {
                        GameManager.respawn.remove(player.getUniqueId().toString());
                        cancel();
                    }
                }
            }.runTaskTimer(Main.instance, 0 , 10);
        }
    }

    @EventHandler
    public void foodLevelChange(FoodLevelChangeEvent event){
        event.setCancelled(true);
    }
}
