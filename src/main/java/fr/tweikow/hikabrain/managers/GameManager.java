package fr.tweikow.hikabrain.managers;

import fr.tweikow.hikabrain.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {

    public static Integer waiting = 0;
    public static Integer waiting_max = 2;
    public static Integer score_blue = 0;
    public static Integer score_red = 0;

    public static HashMap<String, String> players = new HashMap<>();

    public static List<String> waiting_players = new ArrayList<>();
    public static List<String> team_blue = new ArrayList<>();
    public static List<String> team_red = new ArrayList<>();
    public static List<String> spectators = new ArrayList<>();
    public static List<String> respawn = new ArrayList<>();

    public static List<Location> blocks = new ArrayList<>();

    public static void addScore(String team) {
        SettingsManager.teamTeleport();
        if (team.equalsIgnoreCase("bleu")) {
            GameManager.score_blue++;
            Bukkit.broadcastMessage("§eL'équipe §9Bleu §eà marqué ! \n §eScore: §9" + GameManager.score_blue + "§7 : §c" + GameManager.score_red);
        }
        if (team.equalsIgnoreCase("rouge")) {
            GameManager.score_red++;
            Bukkit.broadcastMessage("§eL'équipe §cRouge §eà marqué ! \n §eScore: §9" + GameManager.score_blue + "§7 : §c" + GameManager.score_red);
        }
        if (score_blue == Main.instance.getConfig().getInt("hikabrain.points") || score_red == Main.instance.getConfig().getInt("hikabrain.points")) {
            finishGame(false, null);
            return;
        }
        StateGame.setStatus(StateGame.LAUNCHING);
        SettingsManager.cooldown();
    }

    public static void start() {
        StateGame.setStatus(StateGame.STARTING);
        new BukkitRunnable() {
            int time = 10;
            public void run() {
                if (StateGame.getStatus() != StateGame.STARTING)
                    cancel();
                if (SettingsManager.getWaiting().equals(SettingsManager.getWaitingMax())) {
                    if (time > 0) {
                        Bukkit.broadcastMessage("§eLa partie vas commencer dans " + time + " secondes !");
                        time--;
                    } else {
                        SettingsManager.setTeamToWaiting();
                        cancel();
                    }
                } else {
                    StateGame.setStatus(StateGame.WAITING);
                    Bukkit.broadcastMessage("§cUn joueur est parti. Retour en file d'attente");
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, 20);
    }

    public static void restartGame() {
        waiting = 0;
        score_red = 0;
        score_blue = 0;

        players.clear();
        spectators.clear();
        waiting_players.clear();
        team_red.clear();
        team_blue.clear();

        StateGame.setStatus(StateGame.WAITING);
        new SettingsManager().removeBlocks();

        for (Player player : Bukkit.getOnlinePlayers())
            new PlayerManager().joinWaiting(player);
    }

    public static void finishGame(boolean abandon, String team_win) {
        StateGame.setStatus(StateGame.FINISH);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.setAllowFlight(true);
            if (abandon) {
                if (team_win.equalsIgnoreCase("rouge"))
                    player.sendTitle("§eLa partie est désormais terminé !", "§6Les vainqueurs sont : §7[§cRouge§7]", 15,20*14,15);
                if (team_win.equalsIgnoreCase("bleu"))
                    player.sendTitle("§eLa partie est désormais terminé !", "§6Les vainqueurs sont : §7[§9Bleu§7]", 15, 20*14, 15);
                new BukkitRunnable() {
                    public void run() {
                        restartGame();
                        cancel();
                    }
                }.runTaskTimer(Main.instance, 20*15, 0);
                return;
            }
            if (score_blue > score_red)
                player.sendTitle("§eLa partie est désormais terminé !", "§6Les vainqueurs sont : §7[§9Bleu§7]", 15, 20*14, 15);
            if (score_blue.equals(score_red))
                player.sendTitle("§eLa partie est désormais terminé !", "§6Résultat final : §7[§6Egalité§7]", 15, 20*14, 15);
            else
                player.sendTitle("§eLa partie est désormais terminé !", "§6Les vainqueurs sont : §7[§cRouge§7]", 15,20*14,15);
        }
        new BukkitRunnable() {
            public void run() {
                restartGame();
                cancel();
            }
        }.runTaskLater(Main.instance,20*15);
    }
}
