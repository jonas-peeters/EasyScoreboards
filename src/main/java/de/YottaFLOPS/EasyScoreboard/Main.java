package de.YottaFLOPS.EasyScoreboard;

import com.google.inject.Inject;
import de.YottaFLOPS.EasyScoreboard.Commands.Register;
import de.YottaFLOPS.EasyScoreboard.Replacements.Replacements;
import de.YottaFLOPS.EasyScoreboard.Util.Checks;
import de.YottaFLOPS.EasyScoreboard.Util.Config;
import de.YottaFLOPS.EasyScoreboard.Util.Conversions;
import de.YottaFLOPS.EasyScoreboard.Util.Runnables;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.impl.configs.Messages;
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

@Plugin(
        id = "de_yottaflops_easyscoreboard",
        name = "EasyScoreboards",
        version = "2.2",
        description = "A plugin to easily create scoreboards for lobbys",
        authors = "YottaFLOPS")
public class Main {

    public static List<LineOfString> scoreboardText = new ArrayList<>();
    private final List<String> invalidLineNumbers = new ArrayList<>();
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
    public static int updateTicks = 40;
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
//        if(Checks.checkIfUsedMTime(scoreboardText)) {
//            Runnables.startMTime(this);
//        }
//        if(Checks.checkIfUsedSTime(scoreboardText)) {
//            Runnables.startSTime(this);
//        }
        if (Checks.checkIfUsedPlaceholders(scoreboardText)) {
            Runnables.startPlaceholderTask(this);
        }

    }

    //IDEA says this code is useless (but it isn't)
    @SuppressWarnings("CanBeFinal")
    @Inject
    @DefaultConfig(sharedRoot = true)
    Path defaultConfig;

    //Used for the playercount (to know when it is updated)
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {

        setScoreboard(event.getTargetEntity());

        if(usedPlayerCount) {
            updateAllScoreboards(event.getTargetEntity());
        }
    }

    //Used for the playercount (to know when it is updated)
    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        if (usedPlayerCount && Sponge.getServer().getOnlinePlayers().size() > 0) {
            updateAllScoreboards((Player) Sponge.getServer().getOnlinePlayers().toArray()[0]);
        }
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
        reload();
        event.getCause().first(Player.class).ifPresent((p) -> {
            p.sendMessage(Text.of(TextColors.GRAY, "EasyScoreboard reloaded config successfully"));
        });
    }

    //Generating the scoreboard
    public Scoreboard makeScoreboard(Player player) {

        Scoreboard scoreboard = Scoreboard.builder().build();

        List<Score> lines = new ArrayList<>();
        Objective obj;

        List<TextLine> loadedData = new ArrayList<>();

        for (LineOfString line : scoreboardText) {
            loadedData.add(new TextLine(Replacements.replacePlaceholders(player, line.getNumber(), true),
                    Conversions.lineToText(Replacements.replacePlaceholders(player, line.getText(), false))));
        }

        if (placeholderapiEnabled()) {
            Optional<PlaceholderService> service = Sponge.getGame().getServiceManager().provide(PlaceholderService.class);

            if (service.isPresent()) {
                PlaceholderService placeholderService = service.get();
                for (TextLine line : loadedData) {
                    line.setNumber(placeholderService.replacePlaceholders(line.getNumber(), player).toPlain());

                    List<Text> parts = new ArrayList<>();
                    for (Text text : line.getText().getChildren()) {
                        parts.add(Text.of(text.getColor(), text.getStyle(),
                                placeholderService.replacePlaceholders(text.toPlain(), player)));

                    }
                    line.setText(Text.join(parts));
                }
            }
        }

        for (TextLine line : loadedData) {
            if (line.getText().toPlain().length() > 38) {
                line.setText(Text.of("Line to long error (max: 38)"));
            }
        }

        Text title = Text.of(" ");
        for (TextLine line : loadedData) {
            try {
                if (Integer.parseInt(line.getNumber()) == -1) {
                    title = line.getText();
                    break;
                }
            } catch (Exception e) {
                //Catching exceptions
            }
        }

        obj = Objective.builder()
                .name("EasyScoreboard")
                .criterion(Criteria.DUMMY)
                .displayName(title)
                .build();

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
                    loadedData.get(i).setText(Text.join(loadedData.get(i).getText(), Text.of(" ")));
                }
            }


            int score = 0;
            try {
                score = Integer.parseInt(loadedData.get(i).getNumber());
            } catch (NumberFormatException e) {
                if (!invalidLineNumbers.contains(loadedData.get(i).getNumber())) {
                    logger.error("Line " + loadedData.get(i).getNumber() + " is missing a valid score. If you think " +
                            "this is not your fault please report the following lines to " +
                            "https://github.com/byYottaFLOPS/EasyScoreboards/issues");
                    e.printStackTrace();
                    invalidLineNumbers.add(loadedData.get(i).getNumber());
                }
            }
            if (score != -1) {
                lines.add(obj.getOrCreateScore(loadedData.get(i).getText()));
                lines.get(i-minusOneScoreCount).setScore(score);
            } else {
                minusOneScoreCount++;
            }
        }

        if (!scoreboard.getObjective("EasyScoreboard").isPresent()) {
            scoreboard.addObjective(obj);
        }
        scoreboard.updateDisplaySlot(obj, DisplaySlots.SIDEBAR);

        return scoreboard;
    }

    //Used to reload the config
    public void reload() {
        Config.init();
        Config.load();

        if (Sponge.getServer().getOnlinePlayers().size() != 0) {
            updateAllScoreboards(Sponge.getServer().getOnlinePlayers().iterator().next());
        }

        Runnables.stopTPS();
        Runnables.stopPlaceholderTask();
        if (Checks.checkIfUsedTPS(scoreboardText)) {
            Runnables.startTPS(this);
        }
        if (Checks.checkIfUsedPlaceholders(scoreboardText)) {
            Runnables.startPlaceholderTask(this);
        }
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
        //try {
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
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}

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

    private static boolean placeholderapiEnabled() {
        return Sponge.getPluginManager().getPlugin("placeholderapi").isPresent();
    }
}
