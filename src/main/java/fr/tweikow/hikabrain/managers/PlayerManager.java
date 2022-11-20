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
                    InvManager.sendStuff(player, "red");
                    player.teleport(SettingsManager.spawn_red);
                }
                if (GameManager.team_blue.contains(player.getUniqueId().toString())) {
                    InvManager.sendStuff(player, "blue");
                    player.teleport(SettingsManager.spawn_blue);
                }
                cancel();
            }
        }.runTaskTimer(Main.instance, 0,1);
    }

    public static void addSpectator(Player player) {
        if (!GameManager.spectators.contains(player.getUniqueId().toString()))
            GameManager.spectators.add(player.getUniqueId().toString());
        player.teleport(SettingsManager.spawn_blue);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);

        for (Player p : Bukkit.getOnlinePlayers())
            p.hidePlayer(Main.instance, player);

        if (StateGame.getStatus().equals(StateGame.INGAME) || StateGame.getStatus().equals(StateGame.LAUNCHING))
            Scoreboard.sendInGame(player);
        if (StateGame.getStatus().equals(StateGame.FINISH))
            Scoreboard.sendEndGame(player);

        player.sendMessage("§eVous êtes actuellement Spectateur de la partie.");
    }

    public static void joinInGame(Player player) {
        Scoreboard.sendInGame(player);
        player.setGameMode(GameMode.SURVIVAL);
        String team = GameManager.players.get(player.getUniqueId().toString());
        Bukkit.broadcastMessage(team);
        if (team.equals("red")) {
            GameManager.team_red.add(player.getUniqueId().toString());
            InvManager.sendStuff(player, "red");
            player.teleport(SettingsManager.spawn_red);
            Bukkit.broadcastMessage("§c" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§c" + player.getName());
        }
        if (team.equals("blue")) {
            GameManager.team_blue.add(player.getUniqueId().toString());
            InvManager.sendStuff(player, "blue");
            player.teleport(SettingsManager.spawn_blue);
            Bukkit.broadcastMessage("§3" + player.getName() + " §evient de se reconnecter !");
            player.setPlayerListName("§3" + player.getName());
        }
        if (hasNoTeam(player) && !GameManager.spectators.contains(player.getUniqueId().toString()))
            addSpectator(player);
    }

    public static void joinWaiting(Player player) {

        Scoreboard.sendWaiting(player);
        if (StateGame.getStatus() == StateGame.WAITING) {
            if (!GameManager.waiting_players.contains(player.getUniqueId().toString())) {
                GameManager.waiting_players.add(player.getUniqueId().toString());

                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().clear();
                player.setAllowFlight(false);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 5f);

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
        FastBoard boardWaiting = Scoreboard.boardsWaiting.remove(player.getUniqueId());
        FastBoard boardIG = Scoreboard.boardsInGame.remove(player.getUniqueId());
        if (boardWaiting != null || boardIG != null) {
            boardWaiting.delete();
            boardIG.delete();
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
                        if (StateGame.getStatus() != StateGame.INGAME)
                            cancel();

                        if (i == 0) {
                            Bukkit.broadcastMessage("§cLa partie est désormais terminé ! Raison: Abandon !");

                            if (GameManager.team_blue.size() == 0) {
                                GameManager.finishGame("red");
                                cancel();
                                return;
                            }
                            GameManager.finishGame("blue");
                            cancel();
                            return;
                        }
                        if (i <= 5)
                            Bukkit.broadcastMessage("§cFin de la partie dans " + i + " secondes !");
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
            PlayerManager.teleport(player);
            player.setHealth(20);
            GameManager.respawn.add(player.getUniqueId().toString());
            new BukkitRunnable() {
                int time = 2;
                @Override
                public void run() {
                    if (time > 0)
                        time--;

                    if (time == 0) {
                        player.sendTitle("§eC'est repartit !", "§cMaintenant !", 10, 20, 10);
                        GameManager.respawn.remove(player.getUniqueId().toString());
                        cancel();
                    }
                    player.sendTitle("§eC'est repartit !", "§cDans " + time + " secondes", 10, 20, 10);
                }
            }.runTaskTimer(Main.instance, 0 ,20);
        }
    }
}
