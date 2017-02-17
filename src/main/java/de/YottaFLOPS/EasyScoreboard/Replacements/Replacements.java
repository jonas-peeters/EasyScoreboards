package de.YottaFLOPS.EasyScoreboard.Replacements;

import de.YottaFLOPS.EasyScoreboard.Main;
import de.YottaFLOPS.EasyScoreboard.Utils.Conversions;
import de.YottaFLOPS.EasyScoreboard.Utils.Runnables;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import static de.YottaFLOPS.EasyScoreboard.Utils.Conversions.secondsToTime;
import static de.YottaFLOPS.EasyScoreboard.Main.countdownTimeUse;

public class Replacements {

    public static String replacePlaceholders(Player player, String string, Boolean asNumber) {
        String data = string;
        if (data.contains("%ONLINECOUNT%")) {
            if (removePlaceholders(data.replace("%ONLINECOUNT%", String.valueOf(Sponge.getServer().getOnlinePlayers().size()))).length() < 30) {
                data = data.replace("%ONLINECOUNT%", String.valueOf(Sponge.getServer().getOnlinePlayers().size()));
            } else {
                data = data.replace("%ONLINECOUNT%", "-");
                Main.logger.warn("Line \"" + data + "\" is to long");
            }
        }
        if (data.contains("%PLAYERBALANCE%")) {
            if (removePlaceholders(data.replace("%PLAYERBALANCE%", String.valueOf(Main.getPlayerBalance(player)))).length() < 30) {
                data = data.replace("%PLAYERBALANCE%", String.valueOf(Main.getPlayerBalance(player)));
            } else {
                data = data.replace("%PLAYERBALANCE%", "--");
                Main.logger.warn("Line \"" + data + "\" is to long at player " + player.getName());
            }
        }
        if (data.contains("%PLAYERBALANCEWRAP%")) {
            if (asNumber) {
                data = data.replace("%PLAYERBALANCEWRAP%", "0");
                Main.logger.warn("You can not use %PLAYERBALANCEWRAP% as number");
            } else {
                if (removePlaceholders(data.replace("%PLAYERBALANCEWRAP%", Conversions.intToMoney(Main.getPlayerBalance(player)))).length() < 30) {
                    data = data.replace("%PLAYERBALANCEWRAP%", Conversions.intToMoney(Main.getPlayerBalance(player)));
                } else {
                    data = data.replace("%PLAYERBALANCEWRAP%", "--");
                    Main.logger.warn("Line \"" + data + "\" is to long at player " + player.getName());
                }
            }
        }
        if (data.contains("%PLAYERNAME%")) {
            if (asNumber) {
                data = data.replace("%PLAYERNAME%", "0");
                Main.logger.warn("You can not use %PLAYERNAME% as number");
            } else {
                if (removePlaceholders(data.replace("%PLAYERNAME%", player.getName())).length() < 30) {
                    data = data.replace("%PLAYERNAME%", player.getName());
                } else {
                    data = data.replace("%PLAYERNAME%", "--");
                    Main.logger.warn("Line \"" + data + "\" is to long");
                }
            }
        }
        if (data.contains("%TPS%")) {
            if (asNumber) {
                data = data.replace("%TPS%", String.valueOf(Math.round(Runnables.lastTPS)));
            } else {
                if (removePlaceholders(data.replace("%TPS%", String.valueOf(Math.round(100.0 * Runnables.lastTPS) / 100.0))).length() < 30) {
                    data = data.replace("%TPS%", String.valueOf(Math.round(100.0 * Runnables.lastTPS) / 100.0));
                } else {
                    data = data.replace("%TPS%", "");
                    Main.logger.warn("Line \"" + data + "\" is to long");
                }
            }
        }
        if (data.contains("%COUNTDOWN%")) {
            if (asNumber) {
                data = data.replace("%COUNTDOWN%", String.valueOf(countdownTimeUse));
            } else {
                if (removePlaceholders(data.replace("%COUNTDOWN%", secondsToTime(countdownTimeUse))).length() < 30) {
                    data = data.replace("%COUNTDOWN%", secondsToTime(countdownTimeUse));
                } else {
                    data = data.replace("%COUNTDOWN%", "--");
                    Main.logger.warn("Line \"" + data + "\" is to long");
                }
            }
        }
        if (data.contains("%MTIME%")) {
            if (asNumber) {
                data = data.replace("%MTIME%", Time.getMTimeAsInt());
            } else {
                if (removePlaceholders(data.replace("%MTIME%", Time.getMTime())).length() < 30) {
                    data = data.replace("%MTIME%", Time.getMTime());
                } else {
                    data = data.replace("%MTIME%", "--");
                    Main.logger.warn("Line \"" + data + "\" is to long");
                }
            }
        }
        if (data.contains("%STIME%")) {
            if (asNumber) {
                data = data.replace("%PLAYERNAME%", "0");
                Main.logger.warn("You can not use %STIME% as number");
            } else {
                if (removePlaceholders(data.replace("%STIME%", Time.getSTime())).length() < 30) {
                    data = data.replace("%STIME%", Time.getSTime());
                } else {
                    data = data.replace("%STIME%", "--");
                    Main.logger.warn("Line \"" + data + "\" is to long");
                }
            }
        }

        if (removePlaceholders(data).length() > 29) {
            if (asNumber) {
                return "0";
            } else {
                return "";
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
