package fr.tweikow.hikabrain.commands;

import fr.tweikow.hikabrain.Main;
import fr.tweikow.hikabrain.utils.Manager;
import org.bukkit.ChatColor;
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
                Manager.joinWaiting(player);
            if (args[0].equalsIgnoreCase("leave"))
                Manager.quitWaiting(player);
            if (args[0].equalsIgnoreCase("forcestart"))
                Manager.startGame();
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                if (args[1].equalsIgnoreCase("maxplayers")) {
                    String regex = "[0-9]+";
                    if (args[2].matches(regex)) {
                        Manager.setWaitingMax(Integer.valueOf(args[2]));
                        player.sendMessage(Main.instance.getConfig().getString("messages.setMaxWaitingPlayers").replace('&', '§'));
                        player.sendMessage("§eVous venez de définir le nombre maximum de joueurs");
                    } else {

                    }
                }
                if (args[1].equalsIgnoreCase("team")) {
                    if (args[2].equalsIgnoreCase("bleu")) {
                        Manager.setSpawnTeam(player, "bleu");
                        player.sendMessage("§eVous venez de définir le point d'apparition des §9Bleus");
                    }
                    if (args[2].equalsIgnoreCase("rouge")) {
                        Manager.setSpawnTeam(player, "rouge");
                        player.sendMessage("§eVous venez de définir le point d'apparition des §cRouges");
                    }
                }
                if (args[1].equalsIgnoreCase("team")) {

                }
            }
        }
        return false;
    }
}
