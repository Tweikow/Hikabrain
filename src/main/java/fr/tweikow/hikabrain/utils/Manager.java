package fr.tweikow.hikabrain.utils;

import fr.tweikow.hikabrain.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Manager {

    private static Integer waiting = 0;
    private static Integer waiting_max = 2;
    public static Integer score_blue = 0;
    public static Integer score_red = 0;

    public static HashMap<String, String> players = new HashMap<String, String>();

    public static List<String> waiting_players = new ArrayList<String>();
    public static List<String> team_blue = new ArrayList<String>();
    public static List<String> team_red = new ArrayList<String>();
    public static List<String> spectators = new ArrayList<String>();
    public static List<String> respawn = new ArrayList<String>();

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
        Manager.spectators.add(player.getUniqueId().toString());
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage("§eVous êtes actuellement Spectateur de la partie.");
    }

    public static void addScore(String team) {
        teamTeleport();
        if (team.equalsIgnoreCase("bleu")) {
            Manager.score_blue++;
            Bukkit.broadcastMessage("§eL'équipe §9Bleu §eà marqué ! \n §eScore: §9" + Manager.score_blue + "§7 : §c" + Manager.score_red);
        }
        if (team.equalsIgnoreCase("rouge")) {
            Manager.score_red++;
            Bukkit.broadcastMessage("§eL'équipe §cRouge §eà marqué ! \n §eScore: §9" + Manager.score_blue + "§7 : §c" + Manager.score_red);
        }
        if (score_blue == Main.instance.getConfig().getInt("hikabrain.points") || score_red == Main.instance.getConfig().getInt("hikabrain.points")) {
            finishGame(false, null);
            return;
        }
        StatsGame.setStatus(StatsGame.LAUNCHING);
        cooldown();
    }

    private static void cooldown() {
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
                    StatsGame.setStatus(StatsGame.INGAME);
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, 20);
    }

    public static void joinInGame(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        String team = players.get(player.getUniqueId().toString());
        if (team.equalsIgnoreCase("rouge")) {
            team_red.add(player.getUniqueId().toString());
            InvManager.sendStuff(player, "rouge");
            player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
            Bukkit.broadcastMessage("§c" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§c" + player.getName());
        }
        if (team.equalsIgnoreCase("bleu")) {
            team_blue.add(player.getUniqueId().toString());
            InvManager.sendStuff(player, "bleu");
            player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
            Bukkit.broadcastMessage("§9" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§9" + player.getName());
        }
        if (hasNoTeam(player) && !Manager.spectators.contains(player.getUniqueId().toString()))
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
                if (Manager.getWaiting().equals(Manager.getWaitingMax())) {
                    StatsGame.setStatus(StatsGame.STARTING);
                    new BukkitRunnable() {
                        int time = 10;
                        public void run() {
                            if (StatsGame.getStatus() != StatsGame.STARTING)
                                cancel();
                            if (Manager.getWaiting().equals(Manager.getWaitingMax())) {
                                if (time > 0) {
                                    Bukkit.broadcastMessage("§eLa partie vas commencer dans " + time + " secondes !");
                                    time--;
                                } else {
                                    setTeamToWaiting();
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
            removeTeam(player);
            if (team_blue.size() == 0 || team_red.size() == 0) {
                Bukkit.broadcastMessage("§cSi aucun joueur de l'équipe adverse ne se reconnecte, vous serez vainqueur de la partie !");
                new BukkitRunnable() {
                    int i = 10;
                    public void run() {
                        if (i == 0) {
                            if (team_blue.size() == 0)
                                finishGame(true, "rouge");
                            if (team_red.size() == 0)
                                finishGame(true, "bleu");
                            Bukkit.broadcastMessage("§cLa partie est désormais terminé ! Raison: Abandon !");
                            cancel();
                        }
                        i--;
                    }
                }.runTaskTimer(Main.instance, 0, 20);
            }
        }
        if (Manager.spectators.contains(player.getUniqueId().toString()))
            Manager.spectators.remove(player.getUniqueId().toString());
    }

    public static void finishGame(boolean abandon, String team_win) {
        StatsGame.setStatus(StatsGame.FINISH);
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

    public static void setTeamToWaiting() {
        String value;
        Player player;
        while (!waiting_players.isEmpty()) {
            value = waiting_players.get(new Random().nextInt(waiting_players.size()));
            player = Bukkit.getPlayer(UUID.fromString(value));
            player.setGameMode(GameMode.SURVIVAL);
            if (hasNoTeam(player) && team_red.size() < (waiting_max/2)) {
                team_red.add(value);
                players.put(player.getUniqueId().toString(), "rouge");
                player.setPlayerListName("§c" + Bukkit.getPlayer(UUID.fromString(value)).getName());
                player.sendMessage("§eVous avez rejoint l'équipe §7[§cRouge§7]");
            }
            if (hasNoTeam(player) && team_blue.size() < (waiting_max/2)) {
                team_blue.add(value);
                players.put(player.getUniqueId().toString(), "bleu");
                player.setPlayerListName("§9" + Bukkit.getPlayer(UUID.fromString(value)).getName());
                player.sendMessage("§eVous avez rejoint l'équipe §7[§9Bleu§7]");
            }
            waiting_players.remove(value);
        }
        teamTeleport();
        StatsGame.setStatus(StatsGame.LAUNCHING);
        cooldown();
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

        StatsGame.setStatus(StatsGame.WAITING);
        removeBlocks();

        for (Player player : Bukkit.getOnlinePlayers())
            joinWaiting(player);
    }

    public static void removeTeam(Player player) {
        if (team_blue.contains(player.getUniqueId().toString()))
            team_blue.remove(player.getUniqueId().toString());

        if (team_red.contains(player.getUniqueId().toString()))
            team_red.remove(player.getUniqueId().toString());

        if (spectators.contains(player.getUniqueId().toString()))
            spectators.remove(player.getUniqueId().toString());
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

    public static void playerTeleport(final Player player) {
        player.getInventory().clear();
        new BukkitRunnable() {
            public void run() {
                if (Manager.team_red.contains(player.getUniqueId().toString())) {
                    InvManager.sendStuff(player, "rouge");
                    player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
                }
                if (Manager.team_blue.contains(player.getUniqueId().toString())) {
                    InvManager.sendStuff(player, "bleu");
                    player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
                }
                cancel();
            }
        }.runTaskTimer(Main.instance, 0,1);
    }

    public static void teamTeleport() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            if (Manager.team_red.contains(player.getUniqueId().toString())) {
                InvManager.sendStuff(player, "rouge");
                player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
            }
            if (Manager.team_blue.contains(player.getUniqueId().toString())) {
                InvManager.sendStuff(player, "bleu");
                player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
            }
        }
    }
}
