package fr.tweikow.hikabrain.managers;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.utils.InvManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SettingsManager {

    public static Location spawn_red;
    public static Location spawn_blue;
    public static List<Location> coordonates = new ArrayList<>();

    public static void teamTeleport() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.setHealth(20);
            if (GameManager.team_red.contains(player.getUniqueId().toString())) {
                InvManager.sendStuff(player, "red");
                player.teleport(spawn_red);
            }
            if (GameManager.team_blue.contains(player.getUniqueId().toString())) {
                InvManager.sendStuff(player, "blue");
                player.teleport(spawn_blue);
            }
        }
    }

    public static String getTeam(Player player) {
        if (GameManager.team_blue.contains(player.getUniqueId().toString())) {
            return "blue";
        }
        if (GameManager.team_red.contains(player.getUniqueId().toString())) {
            return "red";
        }
        if (GameManager.spectators.contains(player.getUniqueId().toString())) {
            return "spectator";
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
        if (team.equalsIgnoreCase("blue"))
            Main.instance.getConfig().set("hikabrain.team.blue.spawn", loc);
        if (team.equalsIgnoreCase("red"))
            Main.instance.getConfig().set("hikabrain.team.red.spawn", loc);
        Main.instance.saveConfig();
    }

    public void setWaitingRoom(Player player) {
        Location loc = player.getLocation();
        Main.instance.getConfig().set("hikabrain.waiting_room", loc);
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
                GameManager.players.put(player.getUniqueId().toString(), "red");
                player.setPlayerListName("§c" + Bukkit.getPlayer(UUID.fromString(value)).getName());
                player.sendMessage("§eVous avez rejoint l'équipe §7[§cRouge§7]");
            }
            if (PlayerManager.hasNoTeam(player) && GameManager.team_blue.size() < (GameManager.waiting_max/2)) {
                GameManager.team_blue.add(value);
                GameManager.players.put(player.getUniqueId().toString(), "blue");
                player.setPlayerListName("§9" + Bukkit.getPlayer(UUID.fromString(value)).getName());
                player.sendMessage("§eVous avez rejoint l'équipe §7[§9Bleu§7]");
            }
            GameManager.waiting_players.remove(value);
        }
        teamTeleport();
        StateGame.setStatus(StateGame.LAUNCHING);
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
                    setGamerule(2);
                    StateGame.setStatus(StateGame.INGAME);
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, 20);
    }

    public static void spawnProtect(Location loc, int boucle) {
        Location location = new Location(Bukkit.getWorld(loc.getWorld().getName()), loc.getBlockX(),loc.getBlockY() - 4,loc.getBlockZ());
        for (int i = 0; i < boucle; i++) {
            coordonates.add(new Location(Bukkit.getWorld(location.getWorld().getName()), location.getBlockX(),location.getBlockY() + i,location.getBlockZ()));
            coordonates.add(new Location(Bukkit.getWorld(location.getWorld().getName()), location.getBlockX(),location.getBlockY() + i,location.getBlockZ() + 1));
            coordonates.add(new Location(Bukkit.getWorld(location.getWorld().getName()), location.getBlockX(),location.getBlockY() + i,location.getBlockZ() - 1));
            coordonates.add(new Location(Bukkit.getWorld(location.getWorld().getName()), location.getBlockX() - 1,location.getBlockY() + i,location.getBlockZ()));
            coordonates.add(new Location(Bukkit.getWorld(location.getWorld().getName()), location.getBlockX() + 1,location.getBlockY() + i,location.getBlockZ()));
            coordonates.add(new Location(Bukkit.getWorld(location.getWorld().getName()), location.getBlockX() + 1,location.getBlockY() + i,location.getBlockZ() - 1 ));
            coordonates.add(new Location(Bukkit.getWorld(location.getWorld().getName()), location.getBlockX() + 1,location.getBlockY() + i,location.getBlockZ() + 1 ));
            coordonates.add(new Location(Bukkit.getWorld(location.getWorld().getName()), location.getBlockX() - 1,location.getBlockY() + i,location.getBlockZ() + 1 ));
            coordonates.add(new Location(Bukkit.getWorld(location.getWorld().getName()), location.getBlockX() - 1,location.getBlockY() + i,location.getBlockZ() - 1 ));
        }
    }

    public static void setGamerule(final int seconds) {
        new BukkitRunnable() {
            int cooldown = 0;
            @Override
            public void run() {
                Bukkit.getWorld(Main.instance.getConfig().getString("hikabrain.world")).setGameRule(GameRule.FALL_DAMAGE, false);
                if (cooldown < seconds) cooldown++;
                else {
                    Bukkit.getWorld(Main.instance.getConfig().getString("hikabrain.world")).setGameRule(GameRule.FALL_DAMAGE, true);
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, seconds * 20L);
    }

    public static void pvpCooldown(Player player) {
        double val = 16.0;

        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance == null) throw new RuntimeException("PVPCooldown failed to get player attributes.");
        instance.setBaseValue(val);
        player.saveData();
    }
}
