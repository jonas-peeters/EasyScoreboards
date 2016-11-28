package de.YottaFLOPS.EasyScoreboard.Replacements;

public class Replacements {

    //Replaces all placeholders like colors, styles etc.
    public static String replacePlaceholders(String text) {
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
