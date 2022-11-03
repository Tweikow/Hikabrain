package fr.tweikow.hikabrain;

import fr.tweikow.hikabrain.commands.Hikabrain;
import fr.tweikow.hikabrain.events.*;
import fr.tweikow.hikabrain.managers.GameManager;
import fr.tweikow.hikabrain.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (Main.instance.getConfig().getLocation("hikabrain.team.red.spawn") != null) {
            SettingsManager.spawn_red = Main.instance.getConfig().getLocation("hikabrain.team.red.spawn");
            SettingsManager.spawn_blue = Main.instance.getConfig().getLocation("hikabrain.team.blue.spawn");
            SettingsManager.spawnProtect(SettingsManager.spawn_red, 8);
            SettingsManager.spawnProtect(SettingsManager.spawn_blue, 8);
        } else
            Bukkit.broadcastMessage(ChatColor.RED + "Merci de bien vouloir mettre en place les points de spawn des équipes. Merci de bien vouloir redémarré le serveur après avoir mis les points de spawn des équipes");

        GameManager.resetGame();
        Bukkit.getWorld(Main.instance.getConfig().getString("hikabrain.world")).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        Bukkit.getWorld(Main.instance.getConfig().getString("hikabrain.world")).setGameRule(GameRule.KEEP_INVENTORY, true);

        Bukkit.getPluginManager().registerEvents(new Interact(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(), this);
        Bukkit.getPluginManager().registerEvents(new ChatEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockManager(), this);
        getCommand("hikabrain").setExecutor(new Hikabrain());

        log(this.getName() + " §ais Enable !");
    }

    @Override
    public void onDisable() {

        for (Player player : Bukkit.getOnlinePlayers())
            player.kickPlayer("§cLe serveur redémarre");

        GameManager.resetGame();

        log(this.getName() + " §cis Disable !");
    }

    public static void log(String s) {Bukkit.getConsoleSender().sendMessage(s);}
}
