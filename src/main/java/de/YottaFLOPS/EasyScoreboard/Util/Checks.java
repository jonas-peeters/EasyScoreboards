package de.YottaFLOPS.EasyScoreboard.Util;

import java.util.List;

public class Checks {
    public static boolean checkIfUsedPlayerCount(List<LineOfString> scoreboardText) {
        for(LineOfString line : scoreboardText) {
            if (line.getNumber().contains("%ONLINECOUNT%")) {
                return true;
            }
            if (line.getText().contains("%ONLINECOUNT%")) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfUsedPlayerBalance(List<LineOfString> scoreboardText) {
        for(LineOfString line : scoreboardText) {
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
    public static boolean checkIfUsedTPS(List<LineOfString> scoreboardText) {
        for(LineOfString line : scoreboardText) {
            if (line.getNumber().contains("%TPS%")) {
                return true;
            }
            if (line.getText().contains("%TPS%")) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIfUsedPlaceholders(List<LineOfString> scoreboardText) {
        for(LineOfString line : scoreboardText) {
            if (line.getNumber().contains("%")) {
                return true;
            }
            if (line.getText().contains("%")) {
                return true;
            }
        }
        return false;
    }
}
