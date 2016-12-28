package de.YottaFLOPS.EasyScoreboard;

public class Checks {
    //Checks if Bufferable
    static boolean checkIfBufferable(String[] scoreboardText) {
        for(String s : scoreboardText) {
            if (s.contains("PLAYER")) {
                return false;
            }
        }
        return true;
    }

    static boolean checkIfUsedPlayerCount(String[] scoreboardText) {
        for(String s : scoreboardText) {
            if(s.contains("%ONLINECOUNT%")) {
                return true;
            }
        }
        return false;
    }

    static boolean checkIfUsedPlayerBalance(String[] scoreboardText) {
        for(String s : scoreboardText) {
            if(s.contains("%PLAYERBALANCE%")) {
                return true;
            }
        }
        return false;
    }

    //Check for Runnables
    public static boolean checkIfUsedTPS(String[] scoreboardText) {
        for(String s : scoreboardText) {
            if(s.contains("%TPS%")) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfUsedMTime(String[] scoreboardText) {
        for(String s : scoreboardText) {
            if(s.contains("%MTIME%")) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfUsedSTime(String[] scoreboardText) {
        for(String s : scoreboardText) {
            if(s.contains("%STIME%")) {
                return true;
            }
        }
        return false;
    }
}
