package de.YottaFLOPS.EasyScoreboard.Utils;

import de.YottaFLOPS.EasyScoreboard.Replacements.Colors;
import de.YottaFLOPS.EasyScoreboard.Replacements.Replacements;
import de.YottaFLOPS.EasyScoreboard.Replacements.Styles;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Conversions {
    //Converts a line-string into the Sponge text for multiple colors
    public static Text lineToText(String s) {
        String[] strings = s.split(";");

        List<Text> texts = new ArrayList<>();

        for(String string : strings) {
            texts.add(stringToText(string.replaceAll(";","")));
        }

        return Text.join(texts);
    }

    //Converts a string into the Sponge Text
    private static Text stringToText(String s) {
        return Text.of(Colors.getColor(s), Styles.getStyles(s), Replacements.removePlaceholders(s));
    }

    //Converts a time in seconds into a human readable format
    public static String secondsToTime(int secondsGiven){
        int secondsLeft = secondsGiven;
        String hours = "0";
        String minutes = "0";
        String seconds;
        String time;

        while(secondsLeft >= 3600) {
            hours = String.valueOf(Integer.valueOf(hours) + 1);
            secondsLeft = secondsLeft - 3600;
        }
        while(secondsLeft >= 60) {
            minutes = String.valueOf(Integer.valueOf(minutes) + 1);
            secondsLeft = secondsLeft - 60;
        }

        seconds = String.valueOf(secondsLeft);

        if(hours.length() == 1) {
            hours = "0" + hours;
        }
        if(minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        if(seconds.length() == 1) {
            seconds = "0" + seconds;
        }

        if(!hours.equals("00")) {
            time = hours + ":" + minutes + ":" + seconds;
        } else if(!minutes.equals("00")) {
            time = minutes + ":" + seconds;
        } else {
            time = seconds;
        }

        return  time;
    }

    //Converts a time in ticks into a human readable format
    public static String ticksToTime(long ticksGiven){
        long ticksLeft = ticksGiven;
        String hours = "0";
        String minutes = "0";

        while(ticksLeft >= 1000) {
            hours = String.valueOf(Integer.valueOf(hours) + 1);
            ticksLeft = ticksLeft - 1000;
        }
        while(ticksLeft >= 167) {
            minutes = String.valueOf(Integer.valueOf(minutes) + 10);
            ticksLeft = ticksLeft - 167;
        }

        if(hours.length() == 1) {
            hours = "0" + hours;
        }
        if(minutes.length() == 1) {
            minutes = "0" + minutes;
        }

        return hours + ":" + minutes;
    }

    public static String intToMoney(BigDecimal money) {
        double m = money.doubleValue();
        if (m < 1000) {
            return String.valueOf(m);
        } else if (m < 1000000) {
            return String.valueOf((double) Math.round(m/100)/10) + "k";
        } else if (m < 1000000000) {
            return String.valueOf((double) Math.round(m/100000)/10) + "m";
        } else {
            return String.valueOf((double) Math.round(m/100000000)/10) + "b";
        }
    }

    public static String matchColor(TextColor color) {
        switch (color.getName()) {
            case "BLACK":
                return "&0";
            case "GREEN":
                return "&a";
        }
        return "&f";
    }

    public static String matchStyle(TextStyle style) {
        if (style.isBold().isPresent() && style.isBold().get()) {
            return "&l";
        } else if (style.isObfuscated().isPresent() && style.isObfuscated().get()) {
            return "&k";
        } else if (style.isItalic().isPresent() && style.isItalic().get()) {
            return "&o";
        } else if (style.hasStrikethrough().isPresent() && style.hasStrikethrough().get()) {
            return "&m";
        } else if (style.hasUnderline().isPresent() && style.hasUnderline().get()) {
            return "&n";
        }

        return "";
    }
}
