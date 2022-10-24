package fr.tweikow.hikabrain.utils;

public enum StatsGame {

    WAITING,STARTING,INGAME,FINISH;

    private static StatsGame status;

    public static StatsGame getStatus() {
        return status;
    }
    public static void setStatus(StatsGame status) {
        StatsGame.status = status;
    }
}
