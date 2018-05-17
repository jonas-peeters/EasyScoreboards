package de.YottaFLOPS.EasyScoreboard;

import com.google.inject.Inject;
import de.YottaFLOPS.EasyScoreboard.Commands.Register;
import de.YottaFLOPS.EasyScoreboard.Replacements.Replacements;
import de.YottaFLOPS.EasyScoreboard.Util.*;
import me.rojo8399.placeholderapi.PlaceholderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "de_yottaflops_easyscoreboard",
        name = "EasyScoreboards",
        version = "2.5.1",
        description = "A plugin to easily create scoreboards for lobbys, etc.",
        authors = "YottaFLOPS")
public class Main {

    private final List<String> invalidLineNumbers = new ArrayList<>();
    private Scoreboard bufferedScoreboard;
    public static Logger logger;
    private static EconomyService economyService;
    private Random random;
    public Task countdownTask;


    public Config config;

    //Inits the commands and the logger
    //Starts the config handling
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger = LoggerFactory.getLogger("EasyScoreboards");
        Register.registerCommands(this);

        config = new Config(defaultConfig);
        random = new Random();

        if(Checks.checkIfUsedTPS(config.scoreboardText)) {
            Runnables.startTPS(this);
        }
        if (Checks.checkIfUsedPlaceholders(config.scoreboardText)) {
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

        if(config.usedPlaceholders) {
            updateAllScoreboards(event.getTargetEntity());
        }
    }

    //Used for the playercount (to know when it is updated)
    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        if (config.usedPlaceholders && Sponge.getServer().getOnlinePlayers().size() > 0) {
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
        if(!config.usedPlaceholders) {
            if(Checks.checkIfUsedPlayerBalance(config.scoreboardText)) {
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
        Scoreboard scoreboard = player.getScoreboard();
        Objective obj;
        List<TextLine> loadedData = loadData(player);
        for (TextLine line : loadedData) {
            if (line.getText().toPlain().length() > 38) {
                line.setText(Text.of("Line to long error (max: 38)"));
            }
        }

        Text title = getTitle(loadedData);

        obj = Objective.builder()
                .name("ESB" + random.nextInt(999999999))
                .criterion(Criteria.DUMMY)
                .displayName(title)
                .build();

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
                obj.getOrCreateScore(loadedData.get(i).getText()).setScore(score);
            }
        }


        for (Objective objective : player.getScoreboard().getObjectives()) {
            player.getScoreboard().removeObjective(objective);
        }
        scoreboard.addObjective(obj);
        scoreboard.updateDisplaySlot(obj, DisplaySlots.SIDEBAR);


        Objective tabObjective = makeTabObjective(player);
        scoreboard.addObjective(tabObjective);
        scoreboard.updateDisplaySlot(tabObjective, DisplaySlots.LIST);

        return scoreboard;
    }

    //WIP Tab Scoreboard
    private Objective makeTabObjective(Player player) {
        List<TextLine> loadedData = loadData(player);
        for (TextLine line : loadedData) {
            if (line.getText().toPlain().length() > 38) {
                line.setText(Text.of("Line to long error (max: 38)"));
            }
        }
        Text title = getTitle(loadedData);

        Objective objective = Objective.builder()
                .name("ESBTab")
                .criterion(Criteria.DUMMY)
                .displayName(title)
                .objectiveDisplayMode(ObjectiveDisplayModes.INTEGER)
                .build();

        objective.getOrCreateScore(Text.of("Gigameter")).setScore(2);

        return objective;
    }

    //Used to reload the config
    public void reload() {
        config.load();
        config.save();

        if (Sponge.getServer().getOnlinePlayers().size() != 0) {
            updateAllScoreboards(Sponge.getServer().getOnlinePlayers().iterator().next());
        }

        Runnables.stopTPS();
        Runnables.stopPlaceholderTask();
        if (Checks.checkIfUsedTPS(config.scoreboardText)) {
            Runnables.startTPS(this);
        }
        if (Checks.checkIfUsedPlaceholders(config.scoreboardText)) {
            Runnables.startPlaceholderTask(this);
        }
    }

    //Return the money the players has
    public static BigDecimal getPlayerBalance(Player player) {
        try {
            Optional<UniqueAccount> uOpt = economyService.getOrCreateAccount(player.getUniqueId());
            if (uOpt.isPresent()) {
                UniqueAccount acc = uOpt.get();
                return acc.getBalance(economyService.getDefaultCurrency()).setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception ignored) {
            logger.error("An economy plugin is requested but couldn't be found");
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    //Prepares Scoreboard
    public void updateAllScoreboards(Player player) {
        if (config.usedPlaceholders) {
            bufferedScoreboard = makeScoreboard(player);
        }
        Sponge.getServer().getOnlinePlayers().forEach(this::setScoreboard);
    }

    //Sets the scoreboard
    public void setScoreboard(Player player) {
        if(shouldShow(player)) {
            if (config.usedPlaceholders) {
                player.setScoreboard(bufferedScoreboard);
            } else {
                player.setScoreboard(makeScoreboard(player));
            }
        } else {
            player.setScoreboard(Scoreboard.builder().build());
        }
        if (config.removeOtherTabEntries) {
            for (TabListEntry entry : player.getTabList().getEntries()) {
                player.getTabList().removeEntry(entry.getProfile().getUniqueId());
            }
        }
        if (config.usedTabHeaderOrFooter) {
            Text header = replacePlaceholdersIn(config.tabHeader, player);
            Text footer = replacePlaceholdersIn(config.tabFooter, player);
            player.getTabList().setHeaderAndFooter(header, footer);
        }
    }

    private Text replacePlaceholdersIn(String in, Player player) {
        Text text = Conversions.lineToText(Replacements.replacePlaceholders(player, in, false, config));

        if (placeholderapiEnabled()) {
            Optional<PlaceholderService> service = Sponge.getGame().getServiceManager().provide(PlaceholderService.class);
            if (service.isPresent()) {
                PlaceholderService placeholderService = service.get();

                List<Text> parts = new ArrayList<>();
                for (Text child : text.getChildren()) {
                    parts.add(Text.of(child.getColor(), child.getStyle(), placeholderService.replacePlaceholders(child.toPlain(), player, null)));
                }
                return Text.join(parts);
            }
        }

        return text;
    }

    //Check if scoreboard should be shown to that player
    private boolean shouldShow(Player player) {
        if(!config.showAll) {
            return false;
        }
        for(String s : config.dontShowFor) {
            if(player.getName().equals(s)) {
                return false;
            }
        }
        return true;
    }

    //Check if the PlaceholderAPI is installed
    private static boolean placeholderapiEnabled() {
        return Sponge.getPluginManager().getPlugin("placeholderapi").isPresent();
    }

    private List<TextLine> loadData(Player player) {
        List<TextLine> loadedData = new ArrayList<>();

        for (LineOfString line : config.scoreboardText) {
            loadedData.add(new TextLine(Replacements.replacePlaceholders(player, line.getNumber(), true, config),
                    Conversions.lineToText(Replacements.replacePlaceholders(player, line.getText(), false, config))));
        }

        if (placeholderapiEnabled()) {
            Optional<PlaceholderService> service = Sponge.getGame().getServiceManager().provide(PlaceholderService.class);

            if (service.isPresent()) {
                PlaceholderService placeholderService = service.get();
                for (TextLine line : loadedData) {
                    line.setNumber(placeholderService.replacePlaceholders(line.getNumber(), player, null).toPlain()); // observer = null

                    List<Text> parts = new ArrayList<>();
                    for (Text text : line.getText().getChildren()) {
                        parts.add(Text.of(text.getColor(), text.getStyle(),
                                placeholderService.replacePlaceholders(text.toPlain(), player, null)));

                    }
                    line.setText(Text.join(parts));
                }
            }
        }

        return loadedData;
    }

    private Text getTitle(List<TextLine> lines) {
        for (TextLine line : lines) {
            try {
                if (Integer.parseInt(line.getNumber()) == -1) {
                    return line.getText();
                }
            } catch (Exception e) {
                //Catching exceptions
            }
        }
        return Text.of(" ");
    }
}
