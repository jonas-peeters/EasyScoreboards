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
import org.spongepowered.api.command.CommandSource;
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
import java.util.*;


@Plugin(id = "de.yottaflops.easyscoreboard", name = "Easy Scoreboards", version = "1.3", description = "A plugin to easily create scoreboards for lobbys")
public class Main {

    String[] scoreboardText = new String[]{" "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "};
    private final String[] colorStrings = new String[]{"DARK_AQUA","DARK_BLUE","DARK_GREEN","DARK_RED","DARK_PURPLE","LIGHT_PURPLE","DARK_GRAY","GRAY","WHITE","BLACK","AQUA","BLUE","GOLD","GREEN","YELLOW","RED"};
    private final TextColor[] colors = new TextColor[]{TextColors.DARK_AQUA,TextColors.DARK_BLUE,TextColors.DARK_GREEN,TextColors.DARK_RED,TextColors.DARK_PURPLE,TextColors.LIGHT_PURPLE,TextColors.DARK_GRAY,TextColors.GRAY,TextColors.WHITE,TextColors.BLACK,TextColors.AQUA,TextColors.BLUE,TextColors.GOLD,TextColors.GREEN,TextColors.YELLOW,TextColors.RED};
    private final String[] styleStrings = new String[]{"BOLD","OBFUSCATED","ITALIC","STRIKETHROUGH","UNDERLINE"};
    private final TextStyle[] styles = new TextStyle[]{TextStyles.BOLD,TextStyles.OBFUSCATED,TextStyles.ITALIC,TextStyles.STRIKETHROUGH,TextStyles.UNDERLINE};
    private final String[] placeholders = new String[]{"ONLINECOUNT", "PLAYERNAME", "PLAYERBALANCE"};
    boolean bufferable = true;
    boolean usedPlayerCount = false;
    private Scoreboard bufferedScoreboard;
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ConfigurationNode node;
    private Logger logger;
    private EconomyService economyService;
    int countdownTime = 60;
    int countdownTimeUse = 0;
    String countdownCommand = "";
    Task countdownTask;
    boolean countdownChat;
    boolean countdownXP;
    boolean countdownTitle;

    //Inits the commands and the logger
    //Starts the config handling
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger = LoggerFactory.getLogger("EasyScoreboards");

        HashMap<List<String>, CommandSpec> subcommands = new HashMap<>();
        HashMap<List<String>, CommandSpec> subcommandsCountdown = new HashMap<>();

        subcommandsCountdown.put(Collections.singletonList("set"), CommandSpec.builder()
                .permission("easyscoreboards.countdown.set")
                .description(Text.of("Set the countdown time in seconds"))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("Seconds"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("Command"))))
                .executor(new countdownSet(this))
                .build());

        subcommandsCountdown.put(Collections.singletonList("start"), CommandSpec.builder()
                .permission("easyscoreboards.countdown.start")
                .description(Text.of("Starts the countdown"))
                .executor(new countdownStart(this))
                .build());

        subcommandsCountdown.put(Collections.singletonList("stop"), CommandSpec.builder()
                .permission("easyscoreboards.countdown.stop")
                .description(Text.of("Stops the countdown"))
                .executor(new countdownStop(this))
                .build());

        subcommandsCountdown.put(Collections.singletonList("reset"), CommandSpec.builder()
                .permission("easyscoreboards.countdown.reset")
                .description(Text.of("Resets the countdown"))
                .executor(new countdownReset(this))
                .build());

        subcommandsCountdown.put(Collections.singletonList("xp"), CommandSpec.builder()
                .permission("easyscoreboards.countdown.xp")
                .description(Text.of("Set if there should be a XP countdown"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("true/false"))))
                .executor(new countdownXP(this))
                .build());

        subcommandsCountdown.put(Collections.singletonList("chat"), CommandSpec.builder()
                .permission("easyscoreboards.countdown.chat")
                .description(Text.of("Set if there should be a chat countdown"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("true/false"))))
                .executor(new countdownChat(this))
                .build());

        subcommandsCountdown.put(Collections.singletonList("title"), CommandSpec.builder()
                .permission("easyscoreboards.countdown.title")
                .description(Text.of("Set if there should be a title countdown"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("true/false"))))
                .executor(new countdownTitle(this))
                .build());

        subcommands.put(Collections.singletonList("set"), CommandSpec.builder()
                .permission("easyscoreboards.set")
                .description(Text.of("Change the scoreboard text"))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("Line"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("New Text"))))
                .executor(new setLine(this))
                .build());

        subcommands.put(Collections.singletonList("clear"), CommandSpec.builder()
                .permission("easyscoreboards.clear")
                .description(Text.of("Clear the complete scoreboard"))
                .executor(new clearAll(this))
                .build());

        subcommands.put(Collections.singletonList("reload"), CommandSpec.builder()
                .permission("easyscoreboards.reload")
                .description(Text.of("Reloads the config file"))
                .executor(new reload(this))
                .build());

        subcommands.put(Collections.singletonList("countdown"), CommandSpec.builder()
                .permission("easyscoreboards.countdown.use")
                .description(Text.of("Edit the countdown"))
                .children(subcommandsCountdown)
                .build());

        CommandSpec easyscoreboardsCommandSpec = CommandSpec.builder()
                .extendedDescription(Text.of("Scoreboard Commands"))
                .permission("easyscoreboards.use")
                .children(subcommands)
                .build();

        Sponge.getCommandManager().register(this, easyscoreboardsCommandSpec, "easyscoreboard");

        handleConfig("init");
        handleConfig("load");
        handleConfig("save");
        bufferable = checkIfBufferable();
        usedPlayerCount = checkIfUsedPlayerCount();
    }

    //Used for the playercount (to now when it is updated)
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

    //Used for the playercount (to now when it is updated)
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

    //Sets the economy plugin provider
    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if(event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    //Is called on change of a players balance -> rewrite the scoreboard
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

    //IDEA says this code is useless (but it isn't)
    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    //Handles the config with init, save and load
    void handleConfig(String arg) {
        switch (arg) {
            case "init":

                File config = new File(defaultConfig.toString());

                if(!config.exists()) {
                    logger.warn("Could not find config");
                    try {
                        config.createNewFile();
                        logger.info("Created config file");
                    } catch (IOException e) {
                        logger.error("There was an error creating the config file");
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

                    node.getNode("scoreboard").getNode("countdown").getNode("time").setValue(0);
                    node.getNode("scoreboard").getNode("countdown").getNode("command").setValue("say The countdown is over");
                    node.getNode("scoreboard").getNode("countdown").getNode("chat").setValue(false);
                    node.getNode("scoreboard").getNode("countdown").getNode("xp").setValue(false);
                    node.getNode("scoreboard").getNode("countdown").getNode("title").setValue(false);

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

                    countdownTime = node.getNode("scoreboard").getNode("countdown").getNode("time").getInt();
                    countdownCommand = node.getNode("scoreboard").getNode("countdown").getNode("command").getString();
                    countdownChat = node.getNode("scoreboard").getNode("countdown").getNode("chat").getBoolean();
                    countdownXP = node.getNode("scoreboard").getNode("countdown").getNode("xp").getBoolean();
                    countdownTitle = node.getNode("scoreboard").getNode("countdown").getNode("title").getBoolean();
                    countdownTimeUse = countdownTime;

                    logger.info("Loaded config");
                } catch (Exception e) {
                    logger.error("There was an error reading the config file");
                }
                break;
            case "save":
                try {
                    for(int i = 0; i < 16; i++) {
                        node.getNode("scoreboard", "line", String.valueOf(i)).setValue(scoreboardText[i]);
                    }

                    node.getNode("scoreboard").getNode("countdown").getNode("time").setValue(countdownTime);
                    node.getNode("scoreboard").getNode("countdown").getNode("command").setValue(countdownCommand);
                    node.getNode("scoreboard").getNode("countdown").getNode("chat").setValue(countdownChat);
                    node.getNode("scoreboard").getNode("countdown").getNode("xp").setValue(countdownXP);
                    node.getNode("scoreboard").getNode("countdown").getNode("title").setValue(countdownTitle);

                    configLoader.save(node);
                    logger.info("Saved config");
                } catch (IOException e) {
                    logger.error("There was an error writing to the config file");
                }
                break;
        }
    }

    //Generating the scoreboard
    Scoreboard makeScoreboard(Player player) {
        Scoreboard scoreboard = Scoreboard.builder().build();

        List<Score> lines = new ArrayList<>();
        String[] loadedData = scoreboardText.clone();
        int length = 20;
        Objective obj;


        for(int i = 0; i < loadedData.length; i++) {
            if(loadedData[i].contains("ONLINECOUNT")) {
                if(replacePlaceholders(loadedData[i].replace("ONLINECOUNT", String.valueOf(Sponge.getServer().getOnlinePlayers().size()))).length() < 30) {
                    loadedData[i] = loadedData[i].replace("ONLINECOUNT", String.valueOf(Sponge.getServer().getOnlinePlayers().size()));
                } else {
                    loadedData[i] = loadedData[i].replace("ONLINECOUNT", "-");
                }
            }
            if(loadedData[i].contains("PLAYERBALANCE")) {
                if(replacePlaceholders(loadedData[i].replace("PLAYERBALANCE", String.valueOf(getPlayerBalance(player)))).length() < 30) {
                    loadedData[i] = loadedData[i].replace("PLAYERBALANCE", String.valueOf(getPlayerBalance(player)));
                } else {
                    loadedData[i] = loadedData[i].replace("PLAYERBALANCE", "--");
                }
            }
            if(loadedData[i].contains("PLAYERNAME")) {
                if(replacePlaceholders(loadedData[i].replace("PLAYERNAME", player.getName())).length() < 30) {
                    loadedData[i] = loadedData[i].replace("PLAYERNAME", player.getName());
                } else {
                    loadedData[i] = loadedData[i].replace("PLAYERNAME", "");
                }
            }
            if(loadedData[i].contains("COUNTDOWN")) {
                if(replacePlaceholders(loadedData[i].replace("COUNTDOWN", secondsToTime(countdownTimeUse))).length() < 30) {
                    loadedData[i] = loadedData[i].replace("COUNTDOWN", secondsToTime(countdownTimeUse));
                } else {
                    loadedData[i] = loadedData[i].replace("COUNTDOWN", "--");
                }
            }

            if(replacePlaceholders(loadedData[i]).length() > 29) {
                return Scoreboard.builder().build();
            }
        }

        obj = Objective.builder().name("Test").criterion(Criteria.DUMMY).displayName(lineToTexts(loadedData[0])).build();

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
            lines.add(obj.getOrCreateScore(lineToTexts(loadedData[i])));
            lines.get(i-1).setScore(length-i);
        }

        scoreboard.addObjective(obj);
        scoreboard.updateDisplaySlot(obj, DisplaySlots.SIDEBAR);

        return scoreboard;
    }

    //Filters the string for color names
    private TextColor getColor(String text){
        for(int i = 0; i < 16; i++) {
            if(text.contains(colorStrings[i])){
                return colors[i];
            }
        }
        return TextColors.WHITE;
    }

    //Filters the string for style names
    private TextStyle getStyles(String text) {
        for(int i = 0; i < 5; i++) {
            if(text.contains(styleStrings[i])){
                return styles[i];
            }
        }
        return TextStyles.NONE;
    }

    //Replaces all placeholders like colors, styles etc.
    private String replacePlaceholders(String text) {
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

    //Converts a line-string into the Sponge text for multiple colors
    private Text lineToTexts(String s) {
        String[] strings = s.split(";");

        List<Text> texts = new ArrayList<>();

        for(String string : strings) {
            texts.add(stringToText(string.replaceAll(";","")));
        }

        return Text.join(texts);
    }

    //Converts a string into the Sponge Text
    private Text stringToText(String s) {
        return Text.of(getColor(s), getStyles(s), replacePlaceholders(s));
    }

    //Checks if Bufferable
    private boolean checkIfBufferable() {
        for(String s : scoreboardText) {
            if(s.contains("PLAYER")) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIfUsedPlayerCount() {
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

    //Never Used (could be removed)
    boolean checkIfUsedPlayerName() {
        for(String s : scoreboardText) {
            if(s.contains("PLAYERNAME")) {
                return true;
            }
        }
        return false;
    }

    //Return the money the players has
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

    //Set the Bufferable bool (mostly useless code, but don't want to fix this right now)
    private void setBufferable(boolean bufferable) {
        this.bufferable = bufferable;
    }

    //Is used by all the command which change the text of the lines
    void setLine(String newText, int line, Player player, CommandSource src) {
        if (newText.equals("")) {
            newText = " ";
        }
        try {
            if (line < 16 && line >= 0) {
                if (replacePlaceholders(newText).length() < 30) {
                    scoreboardText[line] = newText;
                    src.sendMessage(Text.of(TextColors.GRAY, "Setting line " + line + " to: " + newText));
                } else {
                    src.sendMessage(Text.of(TextColors.RED, "The length of one line is limited to 30 characters!"));
                }
            } else {
                src.sendMessage(Text.of(TextColors.RED, "You may only use lines between 0 and 15!"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setBufferable(checkIfBufferable());
        usedPlayerCount = checkIfUsedPlayerCount();

        updateScoreboard(player);

        handleConfig("save");
    }

    //Sets a players Scoreboard (after bufferable check)
    void updateScoreboard(Player player) {
        if (bufferable) {
            bufferedScoreboard = makeScoreboard(player);
            for (Player p : Sponge.getServer().getOnlinePlayers()) {
                p.setScoreboard(bufferedScoreboard);
            }
        } else {
            for (Player p : Sponge.getServer().getOnlinePlayers()) {
                p.setScoreboard(makeScoreboard(p));
            }
        }
    }

    //Converts a time in seconds into a human readable format
    private String secondsToTime(int secondsGiven){
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
}
