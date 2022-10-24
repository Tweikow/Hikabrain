package fr.tweikow.hikabrain.utils;

import java.util.ArrayList;
import java.util.List;

public class Manager {

    private static Integer waiting = 0;
    private static Integer waiting_max = 2;
    public static List<String> waiting_players = new ArrayList<String>();
    public static List<String> spectators = new ArrayList<String>();

    public static void setWaiting(Integer waiting) {
        Manager.waiting = waiting;
    }

    public static Integer getWaiting() {
        return waiting;
    }

    public static void setWaitingMax(Integer waiting_max) {
        Manager.waiting_max = waiting_max;
    }

    public static Integer getWaitingMax() {
        return waiting_max;
    }
}
