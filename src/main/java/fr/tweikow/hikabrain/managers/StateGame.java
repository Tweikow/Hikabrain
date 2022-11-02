package fr.tweikow.hikabrain.managers;

import org.bukkit.Bukkit;

import java.util.Set;

public enum StateGame {

    WAITING,STARTING,LAUNCHING,INGAME,FINISH;

    private static StateGame status;

    public static StateGame getStatus() {return status;}

    public static void setStatus(StateGame status) {
        StateGame.status = status;

        if (status == StateGame.LAUNCHING) {
            SettingsManager.cooldown();
            SettingsManager.removeBlocks();
        }
    }
}
