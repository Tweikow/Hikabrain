package fr.tweikow.hikabrain.managers;

import fr.tweikow.hikabrain.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.List;

public class GameManager {

    public static Integer waiting = 0;
    public static Integer waiting_max = 2;
    public static Integer score_blue = 0;
    public static Integer score_red = 0;

    public static HashMap<String, String> players = new HashMap<>();
    public static List<Location> blocks = new ArrayList<>();

    public static List<String> waiting_players = new ArrayList<>();
    public static List<String> team_blue = new ArrayList<>();
    public static List<String> team_red = new ArrayList<>();
    public static List<String> spectators = new ArrayList<>();
    public static List<String> respawn = new ArrayList<>();

    public static void addScore(String team) {
        SettingsManager.teamTeleport();
        if (team.equalsIgnoreCase("blue")) {
            GameManager.score_blue++;
            Bukkit.broadcastMessage("§eL'équipe §9Bleu §eà marqué !");
        }
        if (team.equalsIgnoreCase("red")) {
            GameManager.score_red++;
            Bukkit.broadcastMessage("§eL'équipe §cRouge §eà marqué !");
        }
        if (score_blue == Main.instance.getConfig().getInt("hikabrain.points") || score_red == Main.instance.getConfig().getInt("hikabrain.points")) {
            if (score_blue == Main.instance.getConfig().getInt("hikabrain.points")) {
                finishGame("blue");
            }
            if (score_red == Main.instance.getConfig().getInt("hikabrain.points")) {
                finishGame("red");
            }
            return;
        }
        StateGame.setStatus(StateGame.LAUNCHING);
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

    public static void resetGame() {
        SettingsManager.coordonates.clear();
        if (Main.instance.getConfig().getLocation("hikabrain.team.red.spawn") != null) {
            SettingsManager.spawn_red = Main.instance.getConfig().getLocation("hikabrain.team.red.spawn");
            SettingsManager.spawn_blue = Main.instance.getConfig().getLocation("hikabrain.team.blue.spawn");
            SettingsManager.spawnProtect(SettingsManager.spawn_red, 8);
            SettingsManager.spawnProtect(SettingsManager.spawn_blue, 8);
        } else
            Bukkit.broadcastMessage(ChatColor.RED + "Merci de bien vouloir mettre en place les points de spawn des équipes. Merci de bien vouloir redémarré le serveur après avoir mis les points de spawn des équipes");


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
    }

    public static void restartGame() {
        resetGame();
        for (Player player : Bukkit.getOnlinePlayers())
            PlayerManager.joinWaiting(player);
    }

    public static void finishGame(String team_win) {
        StateGame.setStatus(StateGame.FINISH);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.setAllowFlight(true);
            if (team_win.equalsIgnoreCase("red"))
                player.sendTitle("§eLa partie est désormais terminé !", "§eLes §cRouges §eremportent la partie !", 15, 20 * 14, 15);
            if (team_win.equalsIgnoreCase("blue"))
                player.sendTitle("§eLa partie est désormais terminé !", "§eLes §3Bleus §eremportent la partie !", 15, 20 * 14, 15);
            if (team_win.equalsIgnoreCase("egalite"))
                player.sendTitle("§eLa partie est désormais terminé !", "§6Egalité", 15, 20 * 14, 15);
        }
        new BukkitRunnable() {
            public void run() {
                restartGame();
                cancel();
            }
        }.runTaskLater(Main.instance,20*15);
    }
}
