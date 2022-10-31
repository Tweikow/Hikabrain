package fr.tweikow.hikabrain;

import fr.tweikow.hikabrain.board.FastBoard;
import fr.tweikow.hikabrain.commands.Hikabrain;
import fr.tweikow.hikabrain.events.BlockManager;
import fr.tweikow.hikabrain.events.ChatEvent;
import fr.tweikow.hikabrain.events.PlayerManager;
import fr.tweikow.hikabrain.events.PlayerMove;
import fr.tweikow.hikabrain.utils.StatsGame;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {

    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        StatsGame.setStatus(StatsGame.WAITING);

        Bukkit.getPluginManager().registerEvents(new PlayerManager(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(), this);
        Bukkit.getPluginManager().registerEvents(new ChatEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockManager(), this);
        getCommand("hikabrain").setExecutor(new Hikabrain());

        log(this.getName() + " §ais Enable !");
    }

    @Override
    public void onDisable() {

        log(this.getName() + " §cis Disable !");
    }

    public static void log(String s) {Bukkit.getConsoleSender().sendMessage(s);}
}
