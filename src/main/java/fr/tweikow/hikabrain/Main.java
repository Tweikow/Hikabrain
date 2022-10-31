package fr.tweikow.hikabrain;

import fr.tweikow.hikabrain.commands.Hikabrain;
import fr.tweikow.hikabrain.events.BlockManager;
import fr.tweikow.hikabrain.events.ChatEvent;
import fr.tweikow.hikabrain.events.PlayerEvents;
import fr.tweikow.hikabrain.events.PlayerMove;
import fr.tweikow.hikabrain.managers.StateGame;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        StateGame.setStatus(StateGame.WAITING);

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
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
