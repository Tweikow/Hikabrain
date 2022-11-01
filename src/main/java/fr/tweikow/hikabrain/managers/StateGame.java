package fr.tweikow.hikabrain.managers;

public enum StateGame {

    WAITING,STARTING,LAUNCHING,INGAME,FINISH;

    private static StateGame status;

    public static StateGame getStatus() {return status;}

    public static void setStatus(StateGame status) {
        StateGame.status = status;

        if (status == StateGame.LAUNCHING) {
            SettingsManager.cooldown();
            new SettingsManager().removeBlocks();
        }
    }
}
