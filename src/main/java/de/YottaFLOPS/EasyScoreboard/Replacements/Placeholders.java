package de.YottaFLOPS.EasyScoreboard.Replacements;

class Placeholders {

    private static final String[] placeholders = new String[]{"%ONLINECOUNT%", "%PLAYERNAME%", "%PLAYERBALANCE%",
            "%TPS%", "%STIME%", "%MTIME%"};

    static String[] getPlaceholders() {
        return placeholders;
    }
}
