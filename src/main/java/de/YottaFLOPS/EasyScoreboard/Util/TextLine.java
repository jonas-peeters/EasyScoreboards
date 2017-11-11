package de.YottaFLOPS.EasyScoreboard.Util;

import org.spongepowered.api.text.Text;

public class TextLine {

    private String number;
    private Text text;

    public TextLine(String number, Text text) {
        this.number = number;
        this.text = text;
    }

    public String getNumber() {
        return number;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
