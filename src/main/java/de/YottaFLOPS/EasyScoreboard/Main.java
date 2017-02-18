package de.YottaFLOPS.EasyScoreboard;

import com.google.inject.Inject;
import de.YottaFLOPS.EasyScoreboard.Commands.Register;
import de.YottaFLOPS.EasyScoreboard.Replacements.Replacements;
import de.YottaFLOPS.EasyScoreboard.Utils.Checks;
import de.YottaFLOPS.EasyScoreboard.Utils.Config;
import de.YottaFLOPS.EasyScoreboard.Utils.Conversions;
import de.YottaFLOPS.EasyScoreboard.Utils.Runnables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
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
import java.util.Objects;
import java.util.Optional;

@Plugin(id = "de_yottaflops_easyscoreboard", name = "Easy Scoreboards", version = "2.0.0", description = "A plugin " +
        "to easily create scoreboards for lobbys")
public class Main {

    public static List<Line> scoreboardText = new ArrayList<>();
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
    public static Path normalConfig;

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
        Objective obj;

        List<Line> loadedData = new ArrayList<>();

        for (Line line : scoreboardText) {
            loadedData.add(new Line(Replacements.replacePlaceholders(player, line.getNumber(), true),
                    Replacements.replacePlaceholders(player, line.getText(), false)));
        }

        String title = " ";
        for (Line line : loadedData) {
            try {
                if (Integer.parseInt(line.getNumber()) == -1) {
                    title = line.getText();
                    break;
                }
            } catch (Exception e) {
                //TODO
            }
        }

        obj = Objective.builder()
                .name("EasyScoreboard")
                .criterion(Criteria.DUMMY)
                .displayName(Conversions.lineToText(title))
                .build();

        for (Line line : scoreboardText) {
            if (Objects.equals(line.getText(), "")) {
                line.setText(" ");
            }
        }

        int minusOneScoreCount = 0;

        for (int i = 0; i < loadedData.size(); i++) {
            boolean equalExist = true;
            while (equalExist) {
                equalExist = false;
                for (int j = 0; j < loadedData.size(); j++) {
                    if (i != j && Objects.equals(loadedData.get(i).getText(), loadedData.get(j).getText())) {
                        equalExist = true;
                    }
                }
                if (equalExist) {
                    loadedData.get(i).setText(loadedData.get(i).getText() + " ");
                }
            }


            int score = 0;
            try {
                score = Integer.parseInt(loadedData.get(i).getNumber());
            } catch (NumberFormatException e) {
                logger.error("Line " + i + " is missing a valid score. If you think this is not your fault please " +
                        "report the following lines to https://github.com/byYottaFLOPS/EasyScoreboards/issues");
                e.printStackTrace();
            }
            if (score != -1) {
                lines.add(obj.getOrCreateScore(Conversions.lineToText(loadedData.get(i).getText())));
                lines.get(i-minusOneScoreCount).setScore(score);
            } else {
                minusOneScoreCount++;
            }
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
    public void setLine(String newText, int line, CommandSource commandSource) {
        if (newText.equals("")) {
            newText = " ";
        }
        try {
            if (line < 16 && line >= 0) {
                if (Replacements.removePlaceholders(newText).length() < 30) {
                    scoreboardText.get(line).setText(newText);
                    if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                        commandSource.sendMessage(Text.of(TextColors.GRAY, "Setting line " + line + " to: " + newText));
                    }
                } else {
                    if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                        commandSource.sendMessage(Text.of(TextColors.RED, "The length of one line is limited to 30 characters!"));
                    }
                }
            } else {
                if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                    commandSource.sendMessage(Text.of(TextColors.RED, "You may only use lines between 0 and 15!"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bufferable = Checks.checkIfBufferable(scoreboardText);
        usedPlayerCount = Checks.checkIfUsedPlayerCount(scoreboardText);

        if (Sponge.getServer().getOnlinePlayers().size() > 0) {
            updateAllScoreboards((Player) Sponge.getServer().getOnlinePlayers().toArray()[0]);
        }

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

    public static boolean placeholderapiEnabled() {
        return Sponge.getPluginManager().getPlugin("placeholderapi").isPresent();
    }
}
