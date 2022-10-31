package fr.tweikow.hikabrain.commands;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.managers.GameManager;
import fr.tweikow.hikabrain.managers.SettingsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Hikabrain implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (!player.hasPermission(Main.instance.getConfig().getString("hikabrain.permission"))) {
            player.sendMessage(Main.instance.getConfig().getString("messages.no-permission").replace('&', '§'));
            return false;
        }

        if (args.length == 0) {
            for (String msg : Main.instance.getConfig().getStringList("messages.help"))
                player.sendMessage(msg.replace('&', '§'));
            return false;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("join"))
                GameManager.joinWaiting(player);
            if (args[0].equalsIgnoreCase("leave"))
                GameManager.quit(player);
            if (args[0].equalsIgnoreCase("start"))
                SettingsManager.setTeamToWaiting();
            if (args[0].equalsIgnoreCase("restart"))
                GameManager.restartGame();
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                if (args[1].equalsIgnoreCase("maxplayers")) {
                    String regex = "[0-9]+";
                    if (args[2].matches(regex)) {
                        SettingsManager.setWaitingMax(Integer.valueOf(args[2]));
                        player.sendMessage(Main.instance.getConfig().getString("messages.setMaxWaitingPlayers").replace('&', '§'));
                        player.sendMessage("§eVous venez de définir le nombre maximum de joueurs");
                    }
                }
                if (args[1].equalsIgnoreCase("team")) {
                    if (args[2].equalsIgnoreCase("bleu")) {
                        SettingsManager.setSpawnTeam(player, "bleu");
                        player.sendMessage("§eVous venez de définir le point d'apparition des §9Bleus");
                    }
                    if (args[2].equalsIgnoreCase("rouge")) {
                        SettingsManager.setSpawnTeam(player, "rouge");
                        player.sendMessage("§eVous venez de définir le point d'apparition des §cRouges");
                    }
                }
            }
        }
        return false;
    }
}
