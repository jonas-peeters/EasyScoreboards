package de.YottaFLOPS.EasyScoreboard;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Plugin(id = "de.yottaflops.easyscoreboard", name = "Easy Scoreboards", version = "1.1", description = "A plugin to easily create scoreboards for lobbys")
public class Main {

    String[] scoreboardText = new String[]{" "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "};
    private final String[] colorStrings = new String[]{"DARK_AQUA","DARK_BLUE","DARK_GREEN","DARK_RED","DARK_PURPLE","LIGHT_PURPLE","DARK_GRAY","GRAY","WHITE","BLACK","AQUA","BLUE","GOLD","GREEN","YELLOW","RED"};
    private final TextColor[] colors = new TextColor[]{TextColors.DARK_AQUA,TextColors.DARK_BLUE,TextColors.DARK_GREEN,TextColors.DARK_RED,TextColors.DARK_PURPLE,TextColors.LIGHT_PURPLE,TextColors.DARK_GRAY,TextColors.GRAY,TextColors.WHITE,TextColors.BLACK,TextColors.AQUA,TextColors.BLUE,TextColors.GOLD,TextColors.GREEN,TextColors.YELLOW,TextColors.RED};
    private final String[] styleStrings = new String[]{"BOLD","OBFUSCATED","ITALIC","STRIKETHROUGH","UNDERLINE"};
    private final TextStyle[] styles = new TextStyle[]{TextStyles.BOLD,TextStyles.OBFUSCATED,TextStyles.ITALIC,TextStyles.STRIKETHROUGH,TextStyles.UNDERLINE};
    private final String[] placeholders = new String[]{"ONLINECOUNT", "PLAYERNAME", "PLAYERBALANCE"};
    boolean bufferable = true;
    boolean usedPlayerCount = false;
    Scoreboard bufferedScoreboard;
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ConfigurationNode node;
    private Logger logger;
    private EconomyService economyService;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger = LoggerFactory.getLogger(Main.class);

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

        handleConfig("init");
        handleConfig("load");
        handleConfig("save");
        bufferable = checkIfBufferable();
        usedPlayerCount = checkIfUsedPlayerCount();
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if(bufferable) {
            if(Sponge.getServer().getOnlinePlayers().size() == 1) {
                bufferedScoreboard = makeScoreboard(event.getTargetEntity());
            }
            event.getTargetEntity().setScoreboard(bufferedScoreboard);
        } else {
            event.getTargetEntity().setScoreboard(makeScoreboard(event.getTargetEntity()));
        }
        if(usedPlayerCount) {

            for(Player player : Sponge.getServer().getOnlinePlayers()) {
                if(bufferable) {
                    player.setScoreboard(bufferedScoreboard);
                } else {
                    player.setScoreboard(makeScoreboard(player));
                }
            }
        }
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.execute(new Runnable() {
            @Override
            public void run() {
                for(Player player : Sponge.getServer().getOnlinePlayers()) {
                    if(bufferable) {
                        player.setScoreboard(bufferedScoreboard);
                    } else {
                        player.setScoreboard(makeScoreboard(player));
                    }
                }
            }
        }).delayTicks(10).submit(this);
    }

    @Listener
    public void onChangeServieProvider(ChangeServiceProviderEvent event) {
        if(event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    @Listener
    public void onTransaction(EconomyTransactionEvent event) {
        if(!bufferable) {
            if(checkIfUsedPlayerBalance()) {
                for(Player player : Sponge.getServer().getOnlinePlayers()) {
                    player.setScoreboard(makeScoreboard(player));
                }
            }
        }
    }

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    void handleConfig(String arg) {
        switch (arg) {
            case "init":

                File config = new File(defaultConfig.toString());

                if(!config.exists()) {
                    logger.warn("[EasyScoreboards]: Could not find config");
                    try {
                        config.createNewFile();
                        logger.info("[EasyScoreboards]: Created config file");
                    } catch (IOException e) {
                        logger.error("[EasyScoreboards]: There was an error creating the config file");
                    }

                    configLoader = HoconConfigurationLoader.builder().setPath(defaultConfig).build();
                    node = configLoader.createEmptyNode(ConfigurationOptions.defaults());

                    ConfigurationNode newNode = node.getNode("scoreboard").getNode("line");
                    newNode.getNode("0").setValue("Example Scoreboard");
                    newNode.getNode("1").setValue("This is an");
                    newNode.getNode("2").setValue("example for");
                    newNode.getNode("3").setValue("how you could");
                    newNode.getNode("4").setValue("use this plugin");
                    newNode.getNode("5").setValue(" ");
                    newNode.getNode("6").setValue(" ");
                    newNode.getNode("7").setValue(" ");
                    newNode.getNode("8").setValue(" ");
                    newNode.getNode("9").setValue(" ");
                    newNode.getNode("10").setValue(" ");
                    newNode.getNode("11").setValue(" ");
                    newNode.getNode("12").setValue(" ");
                    newNode.getNode("13").setValue(" ");
                    newNode.getNode("14").setValue(" ");
                    newNode.getNode("15").setValue(" ");

                    try {
                        configLoader.save(node);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    configLoader = HoconConfigurationLoader.builder().setPath(defaultConfig).build();
                    node = configLoader.createEmptyNode(ConfigurationOptions.defaults());
                }



                break;
            case "load":
                try {
                    node = configLoader.load();

                    for(int i = 0; i < 16; i++) {
                        scoreboardText[i] = node.getNode("scoreboard", "line", String.valueOf(i)).getString();
                    }

                    logger.info("[EasyScoreboards]: Loaded config");
                } catch (Exception e) {
                    logger.error("[EasyScoreboards]: There was an error reading the config file");
                }
                break;
            case "save":
                try {


                    for(int i = 0; i < 16; i++) {
                        node.getNode("scoreboard", "line", String.valueOf(i)).setValue(scoreboardText[i]);
                    }
                    configLoader.save(node);
                    logger.info("[EasyScoreboards]: Saved config");
                } catch (IOException e) {
                    logger.error("[EasyScoreboards]: There was an error writing to the config file");
                }
                break;
        }
    }

    Scoreboard makeScoreboard(Player player) {
        Scoreboard scoreboard = Scoreboard.builder().build();

        List<Score> lines = new ArrayList<>();
        String[] loadedData = scoreboardText.clone();
        int length = 20;
        Objective obj;


        for(int i = 0; i < loadedData.length; i++) {
            if(loadedData[i].contains("ONLINECOUNT")) {
                if(replacePlaceholders(loadedData[i].replace("ONLINECOUNT", String.valueOf(Sponge.getServer().getOnlinePlayers().size())), player).length() < 30) {
                    loadedData[i] = loadedData[i].replace("ONLINECOUNT", String.valueOf(Sponge.getServer().getOnlinePlayers().size()));
                } else {
                    loadedData[i] = loadedData[i].replace("ONLINECOUNT", "-");
                }
            }
            if(loadedData[i].contains("PLAYERBALANCE")) {
                if(replacePlaceholders(loadedData[i].replace("PLAYERBALANCE", String.valueOf(getPlayerBalance(player))), player).length() < 30) {
                    loadedData[i] = loadedData[i].replace("PLAYERBALANCE", String.valueOf(getPlayerBalance(player)));
                } else {
                    loadedData[i] = loadedData[i].replace("PLAYERBALANCE", "--");
                }
            }
            if(loadedData[i].contains("PLAYERNAME")) {
                if(replacePlaceholders(loadedData[i].replace("PLAYERNAME", player.getName()), player).length() < 30) {
                    loadedData[i] = loadedData[i].replace("PLAYERNAME", player.getName());
                } else {
                    loadedData[i] = loadedData[i].replace("PLAYERNAME", "");
                }
            }

            if(replacePlaceholders(loadedData[i], player).length() > 29) {
                return Scoreboard.builder().build();
            }
        }

        obj = Objective.builder().name("Test").criterion(Criteria.DUMMY).displayName(Text.of(TextFormat.NONE.color(getColor(loadedData[0])).style(getStyles(loadedData[0])), replacePlaceholders(loadedData[0], player))).build();

        for(int i = 0; i <= 20; i++) {
            if(scoreboardText[i].equals("")) {
                scoreboardText[i] = " ";
            }
        }

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
            lines.add(obj.getOrCreateScore(Text.of(TextFormat.NONE.color(getColor(loadedData[i])).style(getStyles(loadedData[i])), replacePlaceholders(loadedData[i], player))));
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

    String replacePlaceholders(String text, Player player) {
        for(int i = 0; i < 16; i++) {
            if(text.contains(colorStrings[i])){
                text = text.replaceAll(colorStrings[i],"");
            }
        }
        for(int i = 0; i < 5; i++) {
            if(text.contains(styleStrings[i])){
                text = text.replaceAll(styleStrings[i],"");
            }
        }
        for(String s : placeholders) {
            if(text.contains(s)) {
                text = text.replaceAll(s, "");
            }
        }

        return text;
    }

    boolean checkIfBufferable() {
        for(String s : scoreboardText) {
            if(s.contains("PLAYER")) {
                return false;
            }
        }
        return true;
    }

    boolean checkIfUsedPlayerCount() {
        for(String s : scoreboardText) {
            if(s.contains("ONLINECOUNT")) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfUsedPlayerBalance() {
        for(String s : scoreboardText) {
            if(s.contains("PLAYERBALANCE")) {
                return true;
            }
        }
        return false;
    }

    boolean checkIfUsedPlayerName() {
        for(String s : scoreboardText) {
            if(s.contains("PLAYERNAME")) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal getPlayerBalance(Player player) {
        try {
            Optional<UniqueAccount> uOpt = economyService.getOrCreateAccount(player.getUniqueId());
            if (uOpt.isPresent()) {
                UniqueAccount acc = uOpt.get();
                return acc.getBalance(economyService.getDefaultCurrency());
            }
        } catch (Exception ignored) {
        }
        return BigDecimal.ZERO;
    }

    void setBufferable(boolean bufferable) {
        this.bufferable = bufferable;
    }
}
