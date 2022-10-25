package fr.tweikow.hikabrain.utils;

import fr.tweikow.hikabrain.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Manager {

    private static Integer waiting = 0;
    private static Integer waiting_max = 2;

    public static List<String> waiting_players = new ArrayList<String>();
    public static List<String> team_blue = new ArrayList<String>();
    public static List<String> team_red = new ArrayList<String>();
    public static List<String> spectators = new ArrayList<String>();
    public static List<Location> blocks = new ArrayList<Location>();

    public static void setWaiting(Integer waiting) {
        Manager.waiting = waiting;
    }

    public static void setWaitingMax(Integer waiting_max) {
        Manager.waiting_max = waiting_max;
        Main.instance.getConfig().set("hikabrain.waiting_max", waiting_max);
        Main.instance.saveConfig();
    }

    public static void setSpawnTeam(Player player, String team) {
        Location loc = player.getLocation();
        if (team.equalsIgnoreCase("bleu"))
            Main.instance.getConfig().set("hikabrain.team.bleu.spawn", loc);
        if (team.equalsIgnoreCase("rouge"))
            Main.instance.getConfig().set("hikabrain.team.rouge.spawn", loc);
        Main.instance.saveConfig();
    }

    public static String getTeam(Player player) {
        if (team_blue.contains(player.getUniqueId().toString())) {
            return "bleu";
        }
        if (team_red.contains(player.getUniqueId().toString())) {
            return "rouge";
        }
        if (spectators.contains(player.getUniqueId().toString())) {
            return "spectateur";
        }
        return null;
    }

    public static Integer getWaiting() {
        return waiting;
    }

    public static Integer getWaitingMax() {
        return waiting_max;
    }

    public static void addSpectator(Player player) {
        if (!Manager.spectators.contains(player.getUniqueId().toString())) {
            Manager.spectators.add(player.getUniqueId().toString());
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("§eVous êtes actuellement Spectateur de la partie.");
        }
    }

    public static void joinInGame(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        if (Manager.team_red.contains(player.getUniqueId().toString())) {
            InvManager.sendStuff(player, "rouge");
            player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
            Bukkit.broadcastMessage("§c" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§c" + player.getName());
        }
        if (Manager.team_blue.contains(player.getUniqueId().toString())) {
            InvManager.sendStuff(player, "bleu");
            player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
            Bukkit.broadcastMessage("§9" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§9" + player.getName());
        }
        if (!Manager.spectators.contains(player.getUniqueId().toString()))
            addSpectator(player);
    }

    public static void joinWaiting(Player player) {
        if (StatsGame.getStatus() == StatsGame.WAITING) {
            if (!Manager.waiting_players.contains(player.getUniqueId().toString())) {
                player.getInventory().clear();
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 5f);
                Manager.waiting_players.add(player.getUniqueId().toString());
                Manager.setWaiting(Manager.getWaiting() + 1);
                Bukkit.broadcastMessage(player.getName() + " §eà rejoint la file d'attente §7(§6" + Manager.getWaiting() + "§7/§6" + Manager.getWaitingMax() + "§7)");

                player.teleport(new Location(Bukkit.getWorld("world"), -65.305, 16.06250, 251.495, (float) -89.7, (float) 0.0));
                player.setGameMode(GameMode.SURVIVAL);
                if (Manager.getWaiting() == Manager.getWaitingMax()) {
                    StatsGame.setStatus(StatsGame.STARTING);
                    new BukkitRunnable() {
                        int time = 10;

                        public void run() {
                            if (StatsGame.getStatus() != StatsGame.STARTING)
                                cancel();
                            if (Manager.getWaiting() == Manager.getWaitingMax()) {
                                if (time > 0) {
                                    Bukkit.broadcastMessage("§eLa partie vas commencer dans " + time + " secondes !");
                                    time--;
                                } else {
                                    Bukkit.broadcastMessage("§eLa partie commence. Bonne chance !");
                                    StatsGame.setStatus(StatsGame.INGAME);
                                    startGame();
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
        }
    }

    public static void quit(Player player) {
        if (StatsGame.getStatus() == StatsGame.WAITING || StatsGame.getStatus() == StatsGame.STARTING) {
            if (Manager.waiting_players.contains(player.getUniqueId().toString())) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 5f);
                Manager.waiting_players.remove(player.getUniqueId().toString());
                Manager.setWaiting(Manager.getWaiting() - 1);
                Bukkit.broadcastMessage(player.getName() + " §eà quitté la file d'attente §7(§6" + Manager.getWaiting() + "§7/§6" + Manager.getWaitingMax() + "§7)");
            }
            return;
        }
        if (StatsGame.getStatus() == StatsGame.INGAME) {
            Bukkit.broadcastMessage(player.getName() + " §evient de se déconnecter");
            if (team_blue.size() == 0 || team_red.size() == 0)
                Bukkit.broadcastMessage("§cSi aucun joueur de l'équipe adverse ne se reconnecte, vous serez vainqueur de la partie !");
        }
        if (Manager.spectators.contains(player.getUniqueId().toString()))
            Manager.spectators.remove(player.getUniqueId().toString());
    }

    public static void startGame() {
        String value;
        Player player;
        while (!waiting_players.isEmpty()) {
            value = waiting_players.get(new Random().nextInt(waiting_players.size()));
            player = Bukkit.getPlayer(UUID.fromString(value));
            if (hasNoTeam(player) && team_red.size() < (waiting_max/2)) {
                team_red.add(value);
                InvManager.sendStuff(player, "rouge");
                player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
                player.setPlayerListName("§c" + Bukkit.getPlayer(UUID.fromString(value)).getName());
                player.sendTitle("§eVous avez rejoint l'équipe §7[§cRouge§7]", "§c§lBonne Chance !", 15,100,15);
            }
            if (hasNoTeam(player) && team_blue.size() < (waiting_max/2)) {
                team_blue.add(value);
                InvManager.sendStuff(player, "bleu");
                player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
                player.setPlayerListName("§9" + Bukkit.getPlayer(UUID.fromString(value)).getName());
                player.sendTitle("§eVous avez rejoint l'équipe §7[§9Bleu§7]", "§c§lBonne Chance !", 15,100,15);
            }
            waiting_players.remove(value);

        }
        StatsGame.setStatus(StatsGame.INGAME);
    }

    public static void restartGame() {
        spectators.clear();
        waiting_players.clear();
        waiting = 0;
        team_red.clear();
        team_blue.clear();
        StatsGame.setStatus(StatsGame.WAITING);
        removeBlocks();

        for (Player player : Bukkit.getOnlinePlayers())
            joinWaiting(player);
    }

    public static void removeBlocks() {
        if (blocks.isEmpty())
            return;
        for (Location loc : blocks)
            loc.getBlock().setType(Material.AIR);
    }

    public static boolean hasNoTeam(Player player) {
        return !team_blue.contains(player.getUniqueId().toString()) && !team_red.contains(player.getUniqueId().toString());
    }

    public static boolean isColoredWool(Block block, DyeColor color) {
        if (block.getType() == Material.WOOL) {
            Wool wool = (Wool) block.getState().getData();
            return wool.getColor() == color;
        }
        return false;
    }
}
