package fr.tweikow.hikabrain.managers;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.utils.InvManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerManager {

    public static void removeTeam(Player player) {
        if (GameManager.team_blue.contains(player.getUniqueId().toString()))
            GameManager.team_blue.remove(player.getUniqueId().toString());

        if (GameManager.team_red.contains(player.getUniqueId().toString()))
            GameManager.team_red.remove(player.getUniqueId().toString());

        if (GameManager.spectators.contains(player.getUniqueId().toString()))
            GameManager.spectators.remove(player.getUniqueId().toString());
    }

    public static boolean hasNoTeam(Player player) {
        return !GameManager.team_blue.contains(player.getUniqueId().toString()) && !GameManager.team_red.contains(player.getUniqueId().toString());
    }

    public static void playerTeleport(final Player player) {
        player.getInventory().clear();
        new BukkitRunnable() {
            public void run() {
                if (GameManager.team_red.contains(player.getUniqueId().toString())) {
                    InvManager.sendStuff(player, "rouge");
                    player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
                }
                if (GameManager.team_blue.contains(player.getUniqueId().toString())) {
                    InvManager.sendStuff(player, "bleu");
                    player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
                }
                cancel();
            }
        }.runTaskTimer(Main.instance, 0,1);
    }

    public static void addSpectator(Player player) {
        GameManager.spectators.add(player.getUniqueId().toString());
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage("§eVous êtes actuellement Spectateur de la partie.");
    }

    public void joinInGame(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        String team = GameManager.players.get(player.getUniqueId().toString());
        if (team.equalsIgnoreCase("rouge")) {
            GameManager.team_red.add(player.getUniqueId().toString());
            InvManager.sendStuff(player, "rouge");
            player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.rouge.spawn"));
            Bukkit.broadcastMessage("§c" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§c" + player.getName());
        }
        if (team.equalsIgnoreCase("bleu")) {
            GameManager.team_blue.add(player.getUniqueId().toString());
            InvManager.sendStuff(player, "bleu");
            player.teleport((Location) Main.instance.getConfig().get("hikabrain.team.bleu.spawn"));
            Bukkit.broadcastMessage("§9" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§9" + player.getName());
        }
        if (hasNoTeam(player) && !GameManager.spectators.contains(player.getUniqueId().toString()))
            addSpectator(player);
    }

    public void joinWaiting(Player player) {
        if (StateGame.getStatus() == StateGame.WAITING) {
            if (!GameManager.waiting_players.contains(player.getUniqueId().toString())) {
                player.getInventory().clear();
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 5f);
                GameManager.waiting_players.add(player.getUniqueId().toString());
                SettingsManager.setWaiting(SettingsManager.getWaiting() + 1);
                Bukkit.broadcastMessage(player.getName() + " §eà rejoint la file d'attente §7(§6" + SettingsManager.getWaiting() + "§7/§6" + SettingsManager.getWaitingMax() + "§7)");

                player.teleport(new Location(Bukkit.getWorld("world"), -65.305, 16.06250, 251.495, (float) -89.7, (float) 0.0));
                player.setGameMode(GameMode.SURVIVAL);
                if (SettingsManager.getWaiting().equals(SettingsManager.getWaitingMax()))
                    GameManager.start();
            }
        }
    }

    public void quit(Player player) {
        if (StateGame.getStatus() == StateGame.WAITING || StateGame.getStatus() == StateGame.STARTING) {
            if (GameManager.waiting_players.contains(player.getUniqueId().toString())) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 5f);
                GameManager.waiting_players.remove(player.getUniqueId().toString());
                SettingsManager.setWaiting(SettingsManager.getWaiting() - 1);
                Bukkit.broadcastMessage(player.getName() + " §eà quitté la file d'attente §7(§6" + SettingsManager.getWaiting() + "§7/§6" + SettingsManager.getWaitingMax() + "§7)");
            }
            return;
        }
        if (StateGame.getStatus() == StateGame.INGAME) {
            Bukkit.broadcastMessage(player.getName() + " §evient de se déconnecter");
            removeTeam(player);
            if (GameManager.team_blue.size() == 0 || GameManager.team_red.size() == 0) {
                Bukkit.broadcastMessage("§cSi aucun joueur de l'équipe adverse ne se reconnecte, vous serez vainqueur de la partie !");
                new BukkitRunnable() {
                    int i = 10;
                    public void run() {
                        if (i == 0) {
                            if (GameManager.team_blue.size() == 0)
                                GameManager.finishGame(true, "rouge");
                            if (GameManager.team_red.size() == 0)
                                GameManager.finishGame(true, "bleu");
                            Bukkit.broadcastMessage("§cLa partie est désormais terminé ! Raison: Abandon !");
                            cancel();
                        }
                        i--;
                    }
                }.runTaskTimer(Main.instance, 0, 20);
            }
        }
        if (GameManager.spectators.contains(player.getUniqueId().toString()))
            GameManager.spectators.remove(player.getUniqueId().toString());
    }
}
