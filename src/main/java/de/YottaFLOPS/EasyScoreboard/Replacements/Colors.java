package de.YottaFLOPS.EasyScoreboard.Replacements;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class Colors {

    private static final String[] colorStrings = new String[]{"DARK_AQUA","DARK_BLUE","DARK_GREEN","DARK_RED",
            "DARK_PURPLE","LIGHT_PURPLE","DARK_GRAY","GRAY","WHITE","BLACK","AQUA","BLUE","GOLD",
            "GREEN","YELLOW","RED","&3","&1","&2","&4","&5","&d","&8","&7","&f","&0","&b",
            "&9","&6","&a","&e","&c"};
    private static final TextColor[] colors = new TextColor[]{TextColors.DARK_AQUA, TextColors.DARK_BLUE,
            TextColors.DARK_GREEN, TextColors.DARK_RED, TextColors.DARK_PURPLE, TextColors.LIGHT_PURPLE,
            TextColors.DARK_GRAY, TextColors.GRAY, TextColors.WHITE, TextColors.BLACK, TextColors.AQUA, TextColors.BLUE,
            TextColors.GOLD, TextColors.GREEN, TextColors.YELLOW, TextColors.RED,TextColors.DARK_AQUA,
            TextColors.DARK_BLUE, TextColors.DARK_GREEN, TextColors.DARK_RED, TextColors.DARK_PURPLE,
            TextColors.LIGHT_PURPLE, TextColors.DARK_GRAY, TextColors.GRAY, TextColors.WHITE, TextColors.BLACK,
            TextColors.AQUA, TextColors.BLUE, TextColors.GOLD, TextColors.GREEN, TextColors.YELLOW, TextColors.RED};

    //Filters the string for color names
    public static TextColor getColor(String text){
        for(int i = 0; i < colorStrings.length; i++) {
            if(text.contains(colorStrings[i])){
                return colors[i];
            }
        }
        return TextColors.WHITE;
    }

    static String[] getColorStrings() {
        return colorStrings;
    }
}
