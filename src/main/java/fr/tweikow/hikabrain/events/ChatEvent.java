package fr.tweikow.hikabrain.events;

import fr.tweikow.hikabrain.managers.GameManager;
import fr.tweikow.hikabrain.managers.StateGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String team = GameManager.getTeam(player);
        if (StateGame.getStatus() == StateGame.INGAME) {
            if (team.equalsIgnoreCase("bleu"))
                event.setFormat("§9" + player.getName() + " §8≫ §f" + event.getMessage());
            if (team.equalsIgnoreCase("rouge"))
                event.setFormat("§c" + player.getName() + " §8≫ §f" + event.getMessage());
            if (team.equalsIgnoreCase("spectateur")) {
                event.setCancelled(true);
                player.sendMessage("§cLes spectateurs ne peuvent pas communiqué !");
            }
        }
        else
            event.setFormat("§f" + player.getName() + " §8≫ §f" + event.getMessage());
    }
}
