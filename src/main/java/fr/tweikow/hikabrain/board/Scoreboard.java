package fr.tweikow.hikabrain.board;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.managers.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Scoreboard {

    public static Map<UUID, FastBoard> boards = new HashMap<>();

    public static void send(Player player) {
        FastBoard board = new FastBoard(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                String date = simpleDateFormat.format(new Date());

                board.updateTitle("§6§lSolar§f§lMC");
                board.updateLines(ChatColor.GRAY + date,
                        "§1 ",
                        "§cRouge§f: " + GameManager.score_red,
                        "§3Bleu§f: " + GameManager.score_blue,
                        "§2 ",
                        "§7Objectif: " + Main.instance.getConfig().getInt("hikabrain.points") + " §7points",
                        "§3 ",
                        "§7Alliances entre",
                        "§7équipes interdites"
                );

            }
        }.runTaskTimer(Main.instance,0,20);
        if (!boards.containsKey(player.getUniqueId()))
            boards.put(player.getUniqueId(), board);
    }

}
