package de.YottaFLOPS.EasyScoreboard.Replacements;

import de.YottaFLOPS.EasyScoreboard.Main;
import de.YottaFLOPS.EasyScoreboard.Util.Config;
import de.YottaFLOPS.EasyScoreboard.Util.Conversions;
import de.YottaFLOPS.EasyScoreboard.Util.Runnables;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import static de.YottaFLOPS.EasyScoreboard.Util.Conversions.secondsToTime;

public class Replacements {

    public static String replacePlaceholders(Player player, String string, Boolean asNumber, Config config) {
        String data = string;
        if (data.contains("%ONLINECOUNT%")) {
            data = data.replace("%ONLINECOUNT%", String.valueOf(Sponge.getServer().getOnlinePlayers().size()));
        }
        if (data.contains("%PLAYERBALANCE%")) {
            if (asNumber) {
                data = data.replace("%PLAYERBALANCE%", String.valueOf(Main.getPlayerBalance(player).intValue()));
            } else {
                data = data.replace("%PLAYERBALANCE%", String.valueOf(Main.getPlayerBalance(player)));
            }
        }
        if (data.contains("%PLAYERBALANCEWRAP%")) {
            if (asNumber) {
                data = data.replace("%PLAYERBALANCEWRAP%", "0");
                Main.logger.warn("You can not use %PLAYERBALANCEWRAP% as number");
            } else {
                data = data.replace("%PLAYERBALANCEWRAP%", Conversions.intToMoney(Main.getPlayerBalance(player)));
            }
        }
        if (data.contains("%PLAYERNAME%")) {
            if (asNumber) {
                data = data.replace("%PLAYERNAME%", "0");
                Main.logger.warn("You can not use %PLAYERNAME% as number");
            } else {
                data = data.replace("%PLAYERNAME%", player.getName());
            }
        }
        if (data.contains("%TPS%")) {
            if (asNumber) {
                data = data.replace("%TPS%", String.valueOf(Math.round(Runnables.lastTPS)));
            } else {
                data = data.replace("%TPS%", String.valueOf(Math.round(100.0 * Runnables.lastTPS) / 100.0));
            }
        }
        if (data.contains("%COUNTDOWN%")) {
            if (asNumber) {
                data = data.replace("%COUNTDOWN%", String.valueOf(config.countdownTimeUse));
            } else {
                data = data.replace("%COUNTDOWN%", secondsToTime(config.countdownTimeUse));
            }
        }
        if (data.contains("%MTIME%")) {
            if (asNumber) {
                data = data.replace("%MTIME%", Time.getMTimeAsInt());
            } else {
                data = data.replace("%MTIME%", Time.getMTime());
            }
        }
        if (data.contains("%STIME%")) {
            if (asNumber) {
                data = data.replace("%PLAYERNAME%", "0");
                Main.logger.warn("You can not use %STIME% as number");
            } else {
                data = data.replace("%STIME%", Time.getSTime());
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
