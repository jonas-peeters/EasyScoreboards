package de.YottaFLOPS.EasyScoreboard.Replacements;

import de.YottaFLOPS.EasyScoreboard.Util.Conversions;
import org.spongepowered.api.Sponge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class Time {
    static String getMTime() {
        if (Sponge.getServer().getDefaultWorld().isPresent()) {
            long time = (Sponge.getServer().getDefaultWorld().get().getWorldTime() + 6000) % 24000;
            return Conversions.ticksToTime(time);
        } else {
            return "Error";
        }
    }

    static String getMTimeAsInt() {
        if (Sponge.getServer().getDefaultWorld().isPresent()) {
            long time = (Sponge.getServer().getDefaultWorld().get().getWorldTime() + 6000) % 24000;
            return String.valueOf(Math.round(time/1000));
        } else {
            return "0";
        }
    }

    static String getSTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
