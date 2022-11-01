package fr.tweikow.hikabrain.managers;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.board.FastBoard;
import fr.tweikow.hikabrain.board.Scoreboard;
import fr.tweikow.hikabrain.utils.InvManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

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

    public static void teleport(final Player player) {
        player.getInventory().clear();
        new BukkitRunnable() {
            public void run() {
                if (GameManager.team_red.contains(player.getUniqueId().toString())) {
                    InvManager.sendStuff(player, "rouge");
                    player.teleport(SettingsManager.spawn_red);
                }
                if (GameManager.team_blue.contains(player.getUniqueId().toString())) {
                    InvManager.sendStuff(player, "bleu");
                    player.teleport(SettingsManager.spawn_blue);
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

    public static void joinInGame(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        String team = GameManager.players.get(player.getUniqueId().toString());
        if (team.equalsIgnoreCase("rouge")) {
            GameManager.team_red.add(player.getUniqueId().toString());
            InvManager.sendStuff(player, "rouge");
            player.teleport(SettingsManager.spawn_red);
            Bukkit.broadcastMessage("§c" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§c" + player.getName());
        }
        if (team.equalsIgnoreCase("bleu")) {
            GameManager.team_blue.add(player.getUniqueId().toString());
            InvManager.sendStuff(player, "bleu");
            player.teleport(SettingsManager.spawn_blue);
            Bukkit.broadcastMessage("§9" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§9" + player.getName());
        }
        if (hasNoTeam(player) && !GameManager.spectators.contains(player.getUniqueId().toString()))
            addSpectator(player);
    }

    public static void joinWaiting(Player player) {
        if (StateGame.getStatus() == StateGame.WAITING) {
            if (!GameManager.waiting_players.contains(player.getUniqueId().toString())) {
                GameManager.waiting_players.add(player.getUniqueId().toString());

                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().clear();
                player.setAllowFlight(false);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 5f);

                //TODO : Mettre dans la config la loc de la waiting room
                if (Main.instance.getConfig().getLocation("hikabrain.waiting_room") != null)
                    player.teleport(Main.instance.getConfig().getLocation("hikabrain.waiting_room"));
                else
                    player.sendMessage(ChatColor.RED + "La WaitingRoom est pas mise en place !");

                SettingsManager.setWaiting(SettingsManager.getWaiting() + 1);
                player.sendMessage(player.getName() + " §eà rejoint la file d'attente §7(§6" + SettingsManager.getWaiting() + "§7/§6" + SettingsManager.getWaitingMax() + "§7)");

                if (SettingsManager.getWaiting().equals(SettingsManager.getWaitingMax()))
                    GameManager.start();
            }
        }
    }

    public void quit(Player player) {
        FastBoard board = Scoreboard.boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
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
                            Bukkit.broadcastMessage("§cLa partie est désormais terminé ! Raison: Abandon !");

                            if (GameManager.team_blue.size() == 0) {
                                GameManager.finishGame("rouge");
                                cancel();
                                return;
                            }
                            GameManager.finishGame("bleu");
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

    public void respawn(Player player) {
        if (StateGame.getStatus() == StateGame.INGAME) {
            player.getInventory().clear();
            player.setHealth(20);
            PlayerManager.teleport(player);
            SettingsManager.setGamerule(5 + 2);
            /*new BukkitRunnable() {
                int i = 5;
                public void run() {
                    if (i != 0) {
                        i--;
                        player.sendTitle("§6§lPrêt à reprendre ?", "§e" + i + " secondes", 15, 30, 15);
                        return;
                    }
                    player.sendTitle("§eLa partie commence !", "§c§lBonne chance !", 15, 30, 15);
                    GameManager.respawn.remove(player.getUniqueId().toString());
                    cancel();
                }
            }.runTaskTimer(Main.instance, 0 , 20);*/
        }
    }
}
