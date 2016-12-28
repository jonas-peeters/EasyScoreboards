package de.YottaFLOPS.EasyScoreboard.Replacements;

import de.YottaFLOPS.EasyScoreboard.Main;
import de.YottaFLOPS.EasyScoreboard.Runnables;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import static de.YottaFLOPS.EasyScoreboard.Conversions.secondsToTime;
import static de.YottaFLOPS.EasyScoreboard.Main.countdownTimeUse;

public class Replacements {

    public static String[] replacePlaceholders(Player player, String[] loadedData) {
        String[] data = loadedData.clone();
        for (int i = 0; i < data.length; i++) {
            if (data[i].contains("%ONLINECOUNT%")) {
                if (removePlaceholders(data[i].replace("%ONLINECOUNT%", String.valueOf(Sponge.getServer().getOnlinePlayers().size()))).length() < 30) {
                    data[i] = data[i].replace("%ONLINECOUNT%", String.valueOf(Sponge.getServer().getOnlinePlayers().size()));
                } else {
                    data[i] = data[i].replace("%ONLINECOUNT%", "-");
                    Main.logger.warn("Line " + i + " is to long");
                }
            }
            if (data[i].contains("%PLAYERBALANCE%")) {
                if (removePlaceholders(data[i].replace("%PLAYERBALANCE%", String.valueOf(Main.getPlayerBalance(player)))).length() < 30) {
                    data[i] = data[i].replace("%PLAYERBALANCE%", String.valueOf(Main.getPlayerBalance(player)));
                } else {
                    data[i] = data[i].replace("%PLAYERBALANCE%", "--");
                    Main.logger.warn("Line " + i + " is to long");
                }
            }
            if (data[i].contains("%PLAYERNAME%")) {
                if (removePlaceholders(data[i].replace("%PLAYERNAME%", player.getName())).length() < 30) {
                    data[i] = data[i].replace("%PLAYERNAME%", player.getName());
                } else {
                    data[i] = data[i].replace("%PLAYERNAME%", "");
                    Main.logger.warn("Line " + i + " is to long");
                }
            }
            if (data[i].contains("%TPS%")) {
                if (removePlaceholders(data[i].replace("%TPS%", String.valueOf(Math.round(100.0 * Runnables.lastTPS) / 100.0))).length() < 30) {
                    data[i] = data[i].replace("%TPS%", String.valueOf(Math.round(100.0 * Runnables.lastTPS) / 100.0));
                } else {
                    data[i] = data[i].replace("%TPS%", "");
                    Main.logger.warn("Line " + i + " is to long");
                }
            }
            if (data[i].contains("%COUNTDOWN%")) {
                if (removePlaceholders(data[i].replace("%COUNTDOWN%", secondsToTime(countdownTimeUse))).length() < 30) {
                    data[i] = data[i].replace("%COUNTDOWN%", secondsToTime(countdownTimeUse));
                } else {
                    data[i] = data[i].replace("%COUNTDOWN%", "--");
                    Main.logger.warn("Line " + i + " is to long");
                }
            }
            if (data[i].contains("%MTIME%")) {
                if (removePlaceholders(data[i].replace("%MTIME%", Time.getMTime())).length() < 30) {
                    data[i] = data[i].replace("%MTIME%", Time.getMTime());
                } else {
                    data[i] = data[i].replace("%MTIME%", "--");
                    Main.logger.warn("Line " + i + " is to long");
                }
            }
            if (data[i].contains("%STIME%")) {
                if (removePlaceholders(data[i].replace("%STIME%", Time.getSTime())).length() < 30) {
                    data[i] = data[i].replace("%STIME%", Time.getSTime());
                } else {
                    data[i] = data[i].replace("%STIME%", "--");
                    Main.logger.warn("Line " + i + " is to long");
                }
            }

            if (removePlaceholders(data[i]).length() > 29) {
                return new String[20];
            }
        }

        return data;
    }

    //Removes all placeholder-texts like colors, styles etc.
    public static String removePlaceholders(String text) {
        for(int i = 0; i < Colors.getColorStrings().length; i++) {
            if(text.contains(Colors.getColorStrings()[i])){
                text = text.replaceAll(Colors.getColorStrings()[i],"");
            }
        }
        for(int i = 0; i < Styles.getStyleStrings().length; i++) {
            if(text.contains(Styles.getStyleStrings()[i])){
                text = text.replaceAll(Styles.getStyleStrings()[i],"");
            }
        }
        for(String s : Placeholders.getPlaceholders()) {
            if(text.contains(s)) {
                text = text.replaceAll(s, "");
            }
        }

        return text;
    }
}
