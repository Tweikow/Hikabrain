package fr.tweikow.hikabrain.board;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.managers.GameManager;
import fr.tweikow.hikabrain.managers.StateGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.*;

public class Scoreboard {

    public static WeakHashMap<Player, FastBoard> boardsWaiting = new WeakHashMap<>();
    public static WeakHashMap<Player, FastBoard> boardsInGame = new WeakHashMap<>();
    public static WeakHashMap<Player, FastBoard> boardsEndGame = new WeakHashMap<>();

    public static void sendWaiting(Player player) {
        FastBoard board = new FastBoard(player);
        if (!boardsWaiting.containsKey(player))
            boardsWaiting.put(player, board);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (StateGame.getStatus().equals(StateGame.WAITING)) {
                    if (!boardsWaiting.containsKey(player)) {
                        board.delete();
                        cancel();
                    }

                    board.updateTitle("§6§lHikabrain");
                    board.updateLines(ChatColor.GRAY + simpleDateFormat.format(new Date()),
                            "§1 ",
                            ChatColor.YELLOW + "En attente de joueurs...",
                            "§3 ",
                            "§eplay.hikabrain.fr"
                    );
                } else {
                    boardsWaiting.remove(player);
                    sendInGame(player);
                    board.delete();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Main.instance, 0, 5);
    }

    public static void sendInGame(Player player) {
        FastBoard board = new FastBoard(player);
        if (!boardsInGame.containsKey(player))
            boardsInGame.put(player, board);
        if (boardsWaiting.containsKey(player))
            boardsWaiting.remove(player, board);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (StateGame.getStatus().equals(StateGame.LAUNCHING) || StateGame.getStatus().equals(StateGame.INGAME)) {
                    if (!boardsInGame.containsKey(player)) {
                        board.delete();
                        cancel();
                    }

                    board.updateTitle("§6§lHikabrain");
                    board.updateLines(ChatColor.GRAY + simpleDateFormat.format(new Date()),
                            "§1 ",
                            "§cRouge§f: " + GameManager.score_red,
                            "§3Bleu§f: " + GameManager.score_blue,
                            "§2 ",
                            "§7§nObjectif§7: §f" + Main.instance.getConfig().getInt("hikabrain.points") + " §7points",
                            "§3 ",
                            "§eplay.hikabrain.fr"
                    );
                } else {
                    board.delete();
                    boardsInGame.remove(player);
                    Scoreboard.sendEndGame(player);
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Main.instance, 0, 5);
    }

    public static void sendEndGame(Player player) {
        FastBoard board = new FastBoard(player);
        if (!boardsEndGame.containsKey(player))
            boardsEndGame.put(player, board);
        if (boardsInGame.containsKey(player))
            boardsInGame.remove(player, board);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (StateGame.getStatus().equals(StateGame.FINISH)) {
                    if (!boardsEndGame.containsKey(player)) {
                        board.delete();
                        cancel();
                    }

                    board.updateTitle("§6§lHikabrain");
                    board.updateLines(ChatColor.GRAY + simpleDateFormat.format(new Date()),
                            "§1 ",
                            ChatColor.RED + "fin de la partie !",
                            "§3 ",
                            "§eplay.hikabrain.fr"
                    );
                } else {
                    board.delete();
                    boardsEndGame.remove(player);
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Main.instance, 0, 5);
    }

}
