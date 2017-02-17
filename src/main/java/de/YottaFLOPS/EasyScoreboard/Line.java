package de.YottaFLOPS.EasyScoreboard;

public class Line {
    private String number;
    private String text;

    public Line(String number, String text) {
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
}
