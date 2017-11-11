package de.YottaFLOPS.EasyScoreboard.Util;

public class LineOfString {

    private String number;
    private String text;

    public LineOfString(String number, String text) {
        this.number = number;
        this.text = text;
    }

    public String getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
