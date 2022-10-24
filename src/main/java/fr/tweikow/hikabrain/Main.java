package fr.tweikow.hikabrain;

import fr.tweikow.hikabrain.events.PlayerManager;
import fr.tweikow.hikabrain.utils.StatsGame;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        StatsGame.setStatus(StatsGame.WAITING);

        Bukkit.getPluginManager().registerEvents(new PlayerManager(), this);

        log(this.getName() + " §ais Enable !");
    }

    @Override
    public void onDisable() {

        log(this.getName() + " §cis Disable !");
    }

    public static void log(String s) {Bukkit.getConsoleSender().sendMessage(s);}
}
