package de.YottaFLOPS.EasyScoreboard;

import com.google.inject.Inject;
import de.YottaFLOPS.EasyScoreboard.Commands.Register;
import de.YottaFLOPS.EasyScoreboard.Replacements.Replacements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
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
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Plugin(id = "de_yottaflops_easyscoreboard", name = "Easy Scoreboards", version = "1.7.1", description = "A plugin " +
        "to easily create scoreboards for lobbys")
public class Main {

    public static String[] scoreboardText = new String[16];
    public boolean bufferable = true;
    public boolean usedPlayerCount = false;
    private Scoreboard bufferedScoreboard;
    public static Logger logger;
    private static EconomyService economyService;
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

        Register.registerCommands(this);

        Config.init();
        Config.load();
        Config.save();
        bufferable = Checks.checkIfBufferable(scoreboardText);
        usedPlayerCount = Checks.checkIfUsedPlayerCount(scoreboardText);
        if(Checks.checkIfUsedTPS(scoreboardText)) {
            Runnables.startTPS(this);
        }
        if(Checks.checkIfUsedMTime(scoreboardText)) {
            Runnables.startMTime(this);
        }
        if(Checks.checkIfUsedSTime(scoreboardText)) {
            Runnables.startSTime(this);
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
        taskBuilder.execute(() -> {
            for(Player player : Sponge.getServer().getOnlinePlayers()) {
                setScoreboard(player);
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

    //Reload the config and restart the runnables if needed
    @Listener
    public void onReloadEvent(GameReloadEvent event) {
        Config.init();
        Config.load();

        if (Sponge.getServer().getOnlinePlayers().size() != 0) {
            updateAllScoreboards(Sponge.getServer().getOnlinePlayers().iterator().next());
        }

        Runnables.stopTPS();
        Runnables.stopMTime();
        Runnables.stopSTime();
        if (Checks.checkIfUsedTPS(Main.scoreboardText)) {
            Runnables.startTPS(this);
        }
        if (Checks.checkIfUsedMTime(Main.scoreboardText)) {
            Runnables.startMTime(this);
        }
        if (Checks.checkIfUsedSTime(Main.scoreboardText)) {
            Runnables.startSTime(this);
        }
    }

    //Generating the scoreboard
    public Scoreboard makeScoreboard(Player player) {

        Scoreboard scoreboard = Scoreboard.builder().build();

        List<Score> lines = new ArrayList<>();
        int length = 15;
        Objective obj;

        String[] loadedData = Replacements.replacePlaceholders(player, scoreboardText.clone());

        obj = Objective.builder()
                .name("Test")
                .criterion(Criteria.DUMMY)
                .displayName(Conversions.lineToTexts(loadedData[0]))
                .build();

        for(int i = 0; i < scoreboardText.length; i++) {
            if(scoreboardText[i].equals("")) {
                scoreboardText[i] = " ";
            }
        }

        for(int i = scoreboardText.length-1; i >= 0; i--) {
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
    public static BigDecimal getPlayerBalance(Player player) {
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
                if (Replacements.removePlaceholders(newText).length() < 30) {
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
