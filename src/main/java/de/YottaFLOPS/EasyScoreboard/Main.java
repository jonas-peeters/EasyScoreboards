package de.YottaFLOPS.EasyScoreboard;

import com.google.inject.Inject;
import de.YottaFLOPS.EasyScoreboard.Commands.*;
import de.YottaFLOPS.EasyScoreboard.Commands.Countdown.*;
import de.YottaFLOPS.EasyScoreboard.Replacements.Replacements;

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

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;


@Plugin(id = "de_yottaflops_easyscoreboard", name = "Easy Scoreboards", version = "1.6.1", description = "A plugin " +
        "to easily create scoreboards for lobbys")
public class Main {

    public static String[] scoreboardText = new String[]{" "," "," "," "," "," "," "," "," "," "," "," "," "," "," ",
            " "," "," "," "," "," "};
    public boolean bufferable = true;
    public boolean usedPlayerCount = false;
    private Scoreboard bufferedScoreboard;
    private Logger logger;
    private EconomyService economyService;
    public static int countdownTime = 60;
    public static int countdownTimeUse = 0;
    public static String countdownCommand = "";
    public static Task countdownTask;
    public static boolean countdownChat;
    public static boolean countdownXP;
    public static boolean countdownTitle;
    public static boolean showAll = true;
    public static final List<String> dontShowFor = new ArrayList<>();
    static Path normalConfig;

    //Inits the commands and the logger
    //Starts the config handling
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger = LoggerFactory.getLogger("EasyScoreboards");

        normalConfig = defaultConfig;

        HashMap<List<String>, CommandSpec> subcommands = new HashMap<>();
        HashMap<List<String>, CommandSpec> subcommandsCountdown = new HashMap<>();

        subcommandsCountdown.put(Collections.singletonList("set"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.set")
                .description(Text.of("Set the countdown time in seconds"))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("Seconds"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("Command"))))
                .executor(new countdownSet(this))
                .build());

        subcommandsCountdown.put(Collections.singletonList("start"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.start")
                .description(Text.of("Starts the countdown"))
                .executor(new countdownStart(this))
                .build());

        subcommandsCountdown.put(Collections.singletonList("stop"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.stop")
                .description(Text.of("Stops the countdown"))
                .executor(new countdownStop())
                .build());

        subcommandsCountdown.put(Collections.singletonList("reset"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.reset")
                .description(Text.of("Resets the countdown"))
                .executor(new countdownReset())
                .build());

        subcommandsCountdown.put(Collections.singletonList("xp"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.xp")
                .description(Text.of("Set if there should be a XP countdown"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("true/false"))))
                .executor(new countdownXP())
                .build());

        subcommandsCountdown.put(Collections.singletonList("chat"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.chat")
                .description(Text.of("Set if there should be a chat countdown"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("true/false"))))
                .executor(new countdownChat())
                .build());

        subcommandsCountdown.put(Collections.singletonList("title"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.title")
                .description(Text.of("Set if there should be a title countdown"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("true/false"))))
                .executor(new countdownTitle())
                .build());

        subcommands.put(Collections.singletonList("set"), CommandSpec.builder()
                .permission("easyscoreboard.set")
                .description(Text.of("Change the scoreboard text"))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("Line"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("New Text"))))
                .executor(new setLine(this))
                .build());

        subcommands.put(Collections.singletonList("clear"), CommandSpec.builder()
                .permission("easyscoreboard.clear")
                .description(Text.of("Clear the complete scoreboard"))
                .executor(new clearAll(this))
                .build());

        subcommands.put(Collections.singletonList("reload"), CommandSpec.builder()
                .permission("easyscoreboard.reload")
                .description(Text.of("Reloads the config file"))
                .executor(new reload(this))
                .build());

        subcommands.put(Collections.singletonList("countdown"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.use")
                .description(Text.of("Edit the countdown"))
                .children(subcommandsCountdown)
                .build());

        subcommands.put(Collections.singletonList("show"), CommandSpec.builder()
                .permission("easyscoreboard.show")
                .description(Text.of("Enable scoreboard"))
                .executor(new show(this))
                .build());

        subcommands.put(Collections.singletonList("hide"), CommandSpec.builder()
                .permission("easyscoreboard.hide")
                .description(Text.of("Hide scoreboard"))
                .executor(new hide(this))
                .build());

        subcommands.put(Collections.singletonList("showAll"), CommandSpec.builder()
                .permission("easyscoreboard.showAll")
                .description(Text.of("Enable scoreboard for all players (not with private settings)"))
                .executor(new showall(this))
                .build());

        subcommands.put(Collections.singletonList("hideall"), CommandSpec.builder()
                .permission("easyscoreboard.hideall")
                .description(Text.of("Disable scoreboard for all players (also with private settings)"))
                .executor(new hideall(this))
                .build());

        CommandSpec easyscoreboardsCommandSpec = CommandSpec.builder()
                .extendedDescription(Text.of("Scoreboard Commands"))
                .permission("easyscoreboard.use")
                .children(subcommands)
                .build();

        Sponge.getCommandManager().register(this, easyscoreboardsCommandSpec, "easyscoreboard");
        Sponge.getCommandManager().register(this, easyscoreboardsCommandSpec, "esb");

        Config.init();
        Config.load();
        Config.save();
        bufferable = Checks.checkIfBufferable(scoreboardText);
        usedPlayerCount = Checks.checkIfUsedPlayerCount(scoreboardText);
        if(Checks.checkIfUsedTPS(scoreboardText)) {
            TPS.startTPS(this);
        }

    }

    //IDEA says this code is useless (but it isn't)
    @SuppressWarnings("CanBeFinal")
    @Inject
    @DefaultConfig(sharedRoot = true)
    Path defaultConfig;

    //Used for the playercount (to now when it is updated)
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {

        setScoreboard(event.getTargetEntity());

        if(usedPlayerCount) {
            updateAllScoreboards(event.getTargetEntity());
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
                    setScoreboard(player);
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
            if(Checks.checkIfUsedPlayerBalance(scoreboardText)) {
                Sponge.getServer().getOnlinePlayers().forEach(this::setScoreboard);
            }
        }
    }

    //Generating the scoreboard
    public Scoreboard makeScoreboard(Player player) {
        Scoreboard scoreboard = Scoreboard.builder().build();

        List<Score> lines = new ArrayList<>();
        String[] loadedData = scoreboardText.clone();
        int length = 20;
        Objective obj;


        for(int i = 0; i < loadedData.length; i++) {
            if(loadedData[i].contains("ONLINECOUNT")) {
                if(Replacements.replacePlaceholders(loadedData[i].replace("ONLINECOUNT", String.valueOf(Sponge.getServer().getOnlinePlayers().size()))).length() < 30) {
                    loadedData[i] = loadedData[i].replace("ONLINECOUNT", String.valueOf(Sponge.getServer().getOnlinePlayers().size()));
                } else {
                    loadedData[i] = loadedData[i].replace("ONLINECOUNT", "-");
                    logger.warn("Line " + i + " is to long");
                }
            }
            if(loadedData[i].contains("PLAYERBALANCE")) {
                if(Replacements.replacePlaceholders(loadedData[i].replace("PLAYERBALANCE", String.valueOf(getPlayerBalance(player)))).length() < 30) {
                    loadedData[i] = loadedData[i].replace("PLAYERBALANCE", String.valueOf(getPlayerBalance(player)));
                } else {
                    loadedData[i] = loadedData[i].replace("PLAYERBALANCE", "--");
                    logger.warn("Line " + i + " is to long");
                }
            }
            if(loadedData[i].contains("PLAYERNAME")) {
                if(Replacements.replacePlaceholders(loadedData[i].replace("PLAYERNAME", player.getName())).length() < 30) {
                    loadedData[i] = loadedData[i].replace("PLAYERNAME", player.getName());
                } else {
                    loadedData[i] = loadedData[i].replace("PLAYERNAME", "");
                    logger.warn("Line " + i + " is to long");
                }
            }
            if(loadedData[i].contains("TPS")) {
                if(Replacements.replacePlaceholders(loadedData[i].replace("TPS", String.valueOf(Math.round(100.0 * TPS.lastTPS) / 100.0))).length() < 30) {
                    loadedData[i] = loadedData[i].replace("TPS", String.valueOf(Math.round(100.0 * TPS.lastTPS) / 100.0));
                } else {
                    loadedData[i] = loadedData[i].replace("TPS", "");
                    logger.warn("Line " + i + " is to long");
                }
            }
            if(loadedData[i].contains("COUNTDOWN")) {
                if(Replacements.replacePlaceholders(loadedData[i].replace("COUNTDOWN", Conversions.secondsToTime(countdownTimeUse))).length() < 30) {
                    loadedData[i] = loadedData[i].replace("COUNTDOWN", Conversions.secondsToTime(countdownTimeUse));
                } else {
                    loadedData[i] = loadedData[i].replace("COUNTDOWN", "--");
                    logger.warn("Line " + i + " is to long");
                }
            }

            if(Replacements.replacePlaceholders(loadedData[i]).length() > 29) {
                return Scoreboard.builder().build();
            }
        }

        obj = Objective.builder().name("Test").criterion(Criteria.DUMMY).displayName(Conversions.lineToTexts(loadedData[0])).build();

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
            lines.add(obj.getOrCreateScore(Conversions.lineToTexts(loadedData[i])));
            lines.get(i-1).setScore(length-i);
        }

        scoreboard.addObjective(obj);
        scoreboard.updateDisplaySlot(obj, DisplaySlots.SIDEBAR);

        return scoreboard;
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
            logger.error("An economy plugin is requested but couldn't be found");
        }
        return BigDecimal.ZERO;
    }

    //Is used by all the commands which change the text of the lines
    public void setLine(String newText, int line, Player player, CommandSource src) {
        if (newText.equals("")) {
            newText = " ";
        }
        try {
            if (line < 16 && line >= 0) {
                if (Replacements.replacePlaceholders(newText).length() < 30) {
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

        bufferable = Checks.checkIfBufferable(scoreboardText);
        usedPlayerCount = Checks.checkIfUsedPlayerCount(scoreboardText);

        updateAllScoreboards(player);

        Config.save();
    }

    //Prepares Scoreboard
    public void updateAllScoreboards(Player player) {
        if (bufferable) {
            bufferedScoreboard = makeScoreboard(player);
        }
        Sponge.getServer().getOnlinePlayers().forEach(this::setScoreboard);
    }

    //Sets the scoreboard
    public void setScoreboard(Player player) {
        if(Sponge.getServer().getOnlinePlayers().size() == 1) {
            bufferedScoreboard = makeScoreboard(player);
        }
        if(shouldShow(player)) {
            if (bufferable) {
                player.setScoreboard(bufferedScoreboard);
            } else {
                player.setScoreboard(makeScoreboard(player));
            }
        } else {
            player.setScoreboard(Scoreboard.builder().build());
        }
    }

    //Check if scoreboard should be shown to that player
    private boolean shouldShow(Player player) {
        if(!showAll) {
            return false;
        }
        for(String s : dontShowFor) {
            if(player.getName().equals(s)) {
                return false;
            }
        }
        return true;
    }
}
