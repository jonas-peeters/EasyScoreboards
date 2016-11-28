package de.YottaFLOPS.EasyScoreboard.Replacements;

import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

public class Styles {

    private static final String[] styleStrings = new String[]{"BOLD","OBFUSCATED","ITALIC","STRIKETHROUGH","UNDERLINE"};
    private static final TextStyle[] styles = new TextStyle[]{TextStyles.BOLD, TextStyles.OBFUSCATED, TextStyles.ITALIC,
            TextStyles.STRIKETHROUGH, TextStyles.UNDERLINE};

    //Filters the string for style names
    public static TextStyle getStyles(String text) {
        for(int i = 0; i < 5; i++) {
            if(text.contains(styleStrings[i])){
                return styles[i];
            }
        }
        return TextStyles.NONE;
    }

    static String[] getStyleStrings() {
        return styleStrings;
    }
}
