package de.YottaFLOPS.EasyScoreboard.Utils;

import de.YottaFLOPS.EasyScoreboard.Line;

import java.util.List;

public class Checks {
    //Checks if Bufferable
    public static boolean checkIfBufferable(List<Line> scoreboardText) {
        for(Line line : scoreboardText) {
            if (line.getNumber().contains("PLAYER")) {
                return false;
            }
            if (line.getText().contains("PLAYER")) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkIfUsedPlayerCount(List<Line> scoreboardText) {
        for(Line line : scoreboardText) {
            if (line.getNumber().contains("%ONLINECOUNT%")) {
                return false;
            }
            if (line.getText().contains("%ONLINECOUNT%")) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkIfUsedPlayerBalance(List<Line> scoreboardText) {
        for(Line line : scoreboardText) {
            if (line.getNumber().contains("%PLAYERBALANCE%")) {
                return true;
            }
            if (line.getText().contains("%PLAYERBALANCE%")) {
                return true;
            }
            if (line.getNumber().contains("%PLAYERBALANCEWRAP%")) {
                return true;
            }
            if (line.getText().contains("%PLAYERBALANCEWRAP%")) {
                return true;
            }
        }
        return false;
    }

    //Check for Runnables
    public static boolean checkIfUsedTPS(List<Line> scoreboardText) {
        for(Line line : scoreboardText) {
            if (line.getNumber().contains("%TPS%")) {
                return false;
            }
            if (line.getText().contains("%TPS%")) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkIfUsedMTime(List<Line> scoreboardText) {
        for(Line line : scoreboardText) {
            if (line.getNumber().contains("%MTIME%")) {
                return false;
            }
            if (line.getText().contains("%MTIME%")) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkIfUsedSTime(List<Line> scoreboardText) {
        for(Line line : scoreboardText) {
            if (line.getNumber().contains("%STIME%")) {
                return false;
            }
            if (line.getText().contains("%STIME%")) {
                return false;
            }
        }
        return true;
    }
}
