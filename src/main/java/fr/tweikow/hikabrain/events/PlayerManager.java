package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.utils.Manager;
import fr.tweikow.hikabrain.utils.StatsGame;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerManager implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.SURVIVAL);
        event.setJoinMessage("");

        if (StatsGame.getStatus() == StatsGame.WAITING || StatsGame.getStatus() == StatsGame.STARTING) {
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
        }
        if (StatsGame.getStatus() == StatsGame.WAITING) {
            Location waiting_room = new Location(Bukkit.getWorld("world"), -65.305, 16.06250, 251.495, (float) -89.7, (float) 0.0);
            player.teleport(waiting_room);
            if (!Manager.waiting_players.contains(player.getUniqueId().toString())) {
                Manager.waiting_players.add(player.getUniqueId().toString());
                Manager.setWaiting(Manager.getWaiting() + 1);
                Bukkit.broadcastMessage(player.getName() + " §eà rejoint la file d'attente §7(§6" + Manager.getWaiting() + "§7/§6" + Manager.getWaitingMax() + "§7)");

                if (Manager.getWaiting() == Manager.getWaitingMax()) {
                    StatsGame.setStatus(StatsGame.STARTING);
                    new BukkitRunnable() {
                        int time = 10;
                        public void run() {
                            if (Manager.getWaiting() == Manager.getWaitingMax()) {
                                if (time > 0) {
                                    Bukkit.broadcastMessage("§eLa partie vas commencer dans " + time + " secondes !");
                                    time--;
                                }
                                else {
                                    Bukkit.broadcastMessage("§eLa partie commence. Bonne chance !");
                                    StatsGame.setStatus(StatsGame.INGAME);
                                    //Manager.startGame();
                                    cancel();
                                }
                            } else {
                                StatsGame.setStatus(StatsGame.WAITING);
                                Bukkit.broadcastMessage("§cUn joueur est parti. Retour en file d'attente");
                                cancel();
                            }
                        }
                    }.runTaskTimer(Main.instance, 0, 20);
                }
            }
        } else if (!Manager.spectators.contains(player.getUniqueId().toString())) {
            Manager.spectators.add(player.getUniqueId().toString());
            player.setGameMode(GameMode.SPECTATOR);
        } else player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage("");

        if (StatsGame.getStatus() == StatsGame.WAITING || StatsGame.getStatus() == StatsGame.STARTING) {
            if (Manager.waiting_players.contains(player.getUniqueId().toString())) {
                Manager.waiting_players.remove(player.getUniqueId().toString());
                Manager.setWaiting(Manager.getWaiting() - 1);
                Bukkit.broadcastMessage(player.getName() + " §eà quitté la file d'attente §7(§6" + Manager.getWaiting() + "§7/§6" + Manager.getWaitingMax() + "§7)");
            }
        } else if (Manager.spectators.contains(player.getUniqueId().toString()))
            Manager.spectators.remove(player.getUniqueId().toString());
    }
}
