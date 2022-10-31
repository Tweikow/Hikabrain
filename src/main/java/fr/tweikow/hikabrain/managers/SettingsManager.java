package fr.tweikow.hikabrain.managers;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.utils.InvManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

public class SettingsManager {

    public static void teamTeleport() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            if (GameManager.team_red.contains(player.getUniqueId().toString())) {
                InvManager.sendStuff(player, "rouge");
                player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
            }
            if (GameManager.team_blue.contains(player.getUniqueId().toString())) {
                InvManager.sendStuff(player, "bleu");
                player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
            }
        }
    }

    public static String getTeam(Player player) {
        if (GameManager.team_blue.contains(player.getUniqueId().toString())) {
            return "bleu";
        }
        if (GameManager.team_red.contains(player.getUniqueId().toString())) {
            return "rouge";
        }
        if (GameManager.spectators.contains(player.getUniqueId().toString())) {
            return "spectateur";
        }
        return null;
    }

    public static void setWaiting(Integer waiting) {
        GameManager.waiting = waiting;
    }

    public void setWaitingMax(Integer waiting_max) {
        GameManager.waiting_max = waiting_max;
        Main.instance.getConfig().set("hikabrain.waiting_max", waiting_max);
        Main.instance.saveConfig();
    }

    public void setSpawnTeam(Player player, String team) {
        Location loc = player.getLocation();
        if (team.equalsIgnoreCase("bleu"))
            Main.instance.getConfig().set("hikabrain.team.bleu.spawn", loc);
        if (team.equalsIgnoreCase("rouge"))
            Main.instance.getConfig().set("hikabrain.team.rouge.spawn", loc);
        Main.instance.saveConfig();
    }

    public static Integer getWaiting() {
        return GameManager.waiting;
    }

    public static Integer getWaitingMax() {
        return GameManager.waiting_max;
    }

    public static void setTeamToWaiting() {
        String value;
        Player player;
        while (!GameManager.waiting_players.isEmpty()) {
            value = GameManager.waiting_players.get(new Random().nextInt(GameManager.waiting_players.size()));
            player = Bukkit.getPlayer(UUID.fromString(value));
            player.setGameMode(GameMode.SURVIVAL);
            if (PlayerManager.hasNoTeam(player) && GameManager.team_red.size() < (GameManager.waiting_max/2)) {
                GameManager.team_red.add(value);
                GameManager.players.put(player.getUniqueId().toString(), "rouge");
                player.setPlayerListName("§c" + Bukkit.getPlayer(UUID.fromString(value)).getName());
                player.sendMessage("§eVous avez rejoint l'équipe §7[§cRouge§7]");
            }
            if (PlayerManager.hasNoTeam(player) && GameManager.team_blue.size() < (GameManager.waiting_max/2)) {
                GameManager.team_blue.add(value);
                GameManager.players.put(player.getUniqueId().toString(), "bleu");
                player.setPlayerListName("§9" + Bukkit.getPlayer(UUID.fromString(value)).getName());
                player.sendMessage("§eVous avez rejoint l'équipe §7[§9Bleu§7]");
            }
            GameManager.waiting_players.remove(value);
        }
        teamTeleport();
        StateGame.setStatus(StateGame.LAUNCHING);
        cooldown();
    }

    public void removeBlocks() {
        if (GameManager.blocks.isEmpty())
            return;
        for (Location loc : GameManager.blocks)
            loc.getBlock().setType(Material.AIR);
    }

    public static void cooldown() {
        new BukkitRunnable() {
            int i = 5;
            public void run() {
                if (i != 0) {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.sendTitle("§6§lÊtes-vous prêt ?", "§eDébut dans " + i + " secondes", 15, 30, 15);
                    i--;
                }
                else {
                    for (Player p : Bukkit.getOnlinePlayers())
                        p.sendTitle("§eLa partie commence !", "§c§lBonne chance !", 15, 30, 15);
                    StateGame.setStatus(StateGame.INGAME);
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, 20);
    }
}
