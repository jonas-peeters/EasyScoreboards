package de.YottaFLOPS.EasyScoreboard;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Plugin(id = "de.yottaflops.easyscoreboard", name = "Easy Scoreboards", version = "1.0", description = "A plugin to easily create scoreboards for lobbys")
public class Main {

    private File configFile = null;
    String[] scoreboardText = new String[]{" "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "};
    private String[] colorStrings = new String[]{"DARK_AQUA","DARK_BLUE","DARK_GREEN","DARK_RED","DARK_PURPLE","LIGHT_PURPLE","DARK_GRAY","GRAY","WHITE","BLACK","AQUA","BLUE","GOLD","GREEN","YELLOW","RED"};
    private TextColor[] colors = new TextColor[]{TextColors.DARK_AQUA,TextColors.DARK_BLUE,TextColors.DARK_GREEN,TextColors.DARK_RED,TextColors.DARK_PURPLE,TextColors.LIGHT_PURPLE,TextColors.DARK_GRAY,TextColors.GRAY,TextColors.WHITE,TextColors.BLACK,TextColors.AQUA,TextColors.BLUE,TextColors.GOLD,TextColors.GREEN,TextColors.YELLOW,TextColors.RED};
    private String[] styleStrings = new String[]{"BOLD","OBFUSCATED","ITALIC","STRIKETHROUGH","UNDERLINE"};
    private TextStyle[] styles = new TextStyle[]{TextStyles.BOLD,TextStyles.OBFUSCATED,TextStyles.ITALIC,TextStyles.STRIKETHROUGH,TextStyles.UNDERLINE};

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        CommandSpec setLine = CommandSpec.builder()
                .description(Text.of("Change the Scoreboard Text"))
                .permission("easyscoreboard.command.setscoreboard")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("Line"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("New Text"))))
                .executor(new setLine(this))
                .build();

        Sponge.getCommandManager().register(this, setLine, "setscoreboard");

        CommandSpec clearAll = CommandSpec.builder()
                .description(Text.of("Clear the complete Scoreboard"))
                .permission("easyscoreboard.command.clearscoreboard")
                .executor(new clearAll(this))
                .build();

        Sponge.getCommandManager().register(this, clearAll, "clearscoreboard");

        handleConfig(new String[]{"init"});
        handleConfig(new String[]{"load"});
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        event.getTargetEntity().setScoreboard(makeScoreboard(event.getTargetEntity()));
    }

    void handleConfig(String[] args) {
        byte[] exampleContent = "Example\nThis is an example\ncontent for your\nscoreboard\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n".getBytes(Charset.defaultCharset());

        switch (args[0]) {
            case "init":
                configFile = new File(getCurrentPath() + "/scoreboardConfig.conf");
                if (!configFile.exists()) {
                    try {
                        configFile.createNewFile();
                        System.out.println("MainLobbyScoreboard: Created config file!");
                    } catch (IOException e) {
                        System.out.println("MainLobbyScoreboard: There was an error creating the config file!");
                    }
                    try {
                        Files.write(Paths.get(getCurrentPath() + "/scoreboardConfig.conf"), exampleContent, StandardOpenOption.WRITE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "load":
                try {
                    List<String> data;
                    data = Files.readAllLines(configFile.toPath(), Charset.defaultCharset());
                    for (int i = 0; i < data.size(); i++) {
                        scoreboardText[i] = data.get(i);
                    }
                } catch (Exception e) {
                    System.out.println("MainLobbyScoreboard: There was an error reading the config file!");
                }
                break;
            case "save":
                try {
                    List<String> data = new ArrayList<>();
                    Collections.addAll(data, scoreboardText);
                    Files.write(Paths.get(getCurrentPath() + "/scoreboardConfig.conf"), data, Charset.defaultCharset());
                } catch (IOException e) {
                    System.out.println("MainLobbyScoreboard: There was an error writing to the config file!");
                }
                break;
        }
    }

    private String getCurrentPath() {
        String workingDir = System.getProperty("user.dir");
        String[] stringParts = workingDir.split("/");
        String currentPath = "";
        for(int i = 1; i < stringParts.length; i++) {
            currentPath = currentPath + "/" + stringParts[i];
        }
        return currentPath;
    }

    Scoreboard makeScoreboard(Player player) {
        Scoreboard scoreboard = Scoreboard.builder().build();

        List<Score> lines = new ArrayList<>();
        String[] loadedData = scoreboardText.clone();
        int length = 20;
        Objective obj;

        for(int i = 0; i < loadedData.length; i++) {
            if(loadedData[i].contains("PLAYER")) {
                loadedData[i] = loadedData[i].replace("PLAYER", player.getName());
            }
        }

        obj = Objective.builder().name("Test").criterion(Criteria.DUMMY).displayName(Text.of(TextFormat.NONE.color(getColor(loadedData[0])).style(getStyles(loadedData[0])), removeStyleAndColor(loadedData[0]))).build();

        for(int i = 20; i >= 0; i--) {
            if(scoreboardText[i].equals(" ")) {
                length--;
            }
            if(!scoreboardText[i].equals(" ")) {
                i = 0;
            }
        }

        for(int i = 1; i <= length; i++) {
            boolean doesNotEqual = false;
            while(!doesNotEqual) {
                doesNotEqual = true;
                for (int i2 = 1; i2 <= length; i2++) {
                    if (i2 != i) {
                        if (loadedData[i].equals(loadedData[i2])) {
                            loadedData[i] = loadedData[i] + " ";
                            doesNotEqual = false;
                        }
                    }
                }
            }
            lines.add(obj.getOrCreateScore(Text.of(TextFormat.NONE.color(getColor(loadedData[i])).style(getStyles(loadedData[i])), removeStyleAndColor(loadedData[i]))));
            lines.get(i-1).setScore(length-i);
        }

        scoreboard.addObjective(obj);
        scoreboard.updateDisplaySlot(obj, DisplaySlots.SIDEBAR);

        return scoreboard;
    }

    private TextColor getColor(String text){
        for(int i = 0; i < 16; i++) {
            if(text.contains(colorStrings[i])){
                return colors[i];
            }
        }
        return TextColors.WHITE;
    }

    private TextStyle getStyles(String text) {
        for(int i = 0; i < 5; i++) {
            if(text.contains(styleStrings[i])){
                return styles[i];
            }
        }
        return TextStyles.NONE;
    }

    private String removeStyleAndColor(String text) {
        for(int i = 0; i < 16; i++) {
            if(text.contains(colorStrings[i])){
                text = text.replace(colorStrings[i],"");
            }
        }
        for(int i = 0; i < 5; i++) {
            if(text.contains(styleStrings[i])){
                text = text.replace(styleStrings[i],"");
            }
        }
        return text;
    }
}
