package de.YottaFLOPS.EasyScoreboard.Util;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static Logger logger;
    private static ConfigurationLoader<CommentedConfigurationNode> configLoader;

    public boolean showAll = true;
    public int countdownTime = 11;
    public String countdownCommand = "";
    public boolean countdownChat = true;
    public boolean countdownXP = false;
    public boolean countdownTitle = true;
    public int countdownTimeUse = countdownTime;
    public final List<String> dontShowFor = new ArrayList<>();
    public List<LineOfString> scoreboardText = new ArrayList<>();
    public int updateTicks = 40;
    public String tabHeader = "";
    public String tabFooter = "";
    public boolean removeOtherTabEntries = false;
    public boolean usedTabHeaderOrFooter = false;
    public boolean usedPlaceholders = false;

    public Config(Path configPath) {
        configLoader = HoconConfigurationLoader.builder().setPath(configPath).build();
        logger = LoggerFactory.getLogger("EasyScoreboard: Config");
        load();
        save();
    }

    public void load() {
        try {
            ConfigurationNode node = configLoader.load();

            scoreboardText.clear();
            List<? extends ConfigurationNode> nodeList = node.getNode("scoreboard").getNode("lines").getChildrenList();
            for (ConfigurationNode n : nodeList) {
                String out = n.getValue().toString();
                String number = out.split(",")[0].split("=")[1];
                String text = out.split(",")[1].split("=")[1].replaceAll("}", "");
                scoreboardText.add(new LineOfString(number, text));
            }
            usedPlaceholders = Checks.checkIfUsedPlaceholders(scoreboardText);

            updateTicks = node.getNode("scoreboard").getNode("placeholderUpdateTicks").getInt();
            if (updateTicks == 0) {
                updateTicks = 20;
            }
            showAll = node.getNode("scoreboard").getNode("showForAll").getBoolean();
            countdownTime = node.getNode("scoreboard").getNode("countdown").getNode("time").getInt();
            countdownCommand = node.getNode("scoreboard").getNode("countdown").getNode("command").getString();
            if (countdownCommand == null) {
                countdownCommand = "";
            }
            countdownChat = node.getNode("scoreboard").getNode("countdown").getNode("chat").getBoolean();
            countdownXP = node.getNode("scoreboard").getNode("countdown").getNode("xp").getBoolean();
            countdownTitle = node.getNode("scoreboard").getNode("countdown").getNode("title").getBoolean();
            countdownTimeUse = countdownTime;

            tabHeader = node.getNode("scoreboard").getNode("tabHeader").getString();
            if (tabHeader == null) {
                tabHeader = "";
            }
            tabFooter = node.getNode("scoreboard").getNode("tabFooter").getString();
            if (tabFooter == null) {
                tabFooter = "";
            }
            usedTabHeaderOrFooter = !tabFooter.equals("") || !tabHeader.equals("");
            removeOtherTabEntries = node.getNode("scoreboard").getNode("tabRemovePlayerNames").getBoolean();

            String hideFor = node.getNode("scoreboard").getNode("hideFor").getString();
            if (hideFor == null) {
                hideFor = "";
            }
            String[] hideForSplit = hideFor.split(" ");

            dontShowFor.clear();
            for(String s : hideForSplit) {
                if (!s.equals(" ") && !s.equals("")) {
                    dontShowFor.add(s.replace(" ", ""));
                }
            }

            logger.info("Loaded config");
        } catch (Exception e) {
            logger.error("There was an error reading the config file");
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            ConfigurationNode node = configLoader.load();

            List<String> list = new ArrayList<>();

            for (LineOfString line : scoreboardText) {
                list.add("number=" + line.getNumber() + ", text=" + line.getText());
            }

            node.getNode("scoreboard").getNode("lines").setValue(new TypeToken<List<String>>() {}, list);

            node.getNode("scoreboard").getNode("showForAll").setValue(showAll);
            node.getNode("scoreboard").getNode("placeholderUpdateTicks").setValue(updateTicks);

            node.getNode("scoreboard").getNode("countdown").getNode("time").setValue(countdownTime);
            node.getNode("scoreboard").getNode("countdown").getNode("command").setValue(countdownCommand);
            node.getNode("scoreboard").getNode("countdown").getNode("chat").setValue(countdownChat);
            node.getNode("scoreboard").getNode("countdown").getNode("xp").setValue(countdownXP);
            node.getNode("scoreboard").getNode("countdown").getNode("title").setValue(countdownTitle);

            node.getNode("scoreboard").getNode("tabHeader").setValue(tabHeader);
            node.getNode("scoreboard").getNode("tabFooter").setValue(tabFooter);
            node.getNode("scoreboard").getNode("tabRemovePlayerNames").setValue(removeOtherTabEntries);

            StringBuilder hideFor = new StringBuilder();
            for(String s : dontShowFor) {
                hideFor.append(" ").append(s);
            }

            node.getNode("scoreboard").getNode("hideFor").setValue(hideFor.toString());

            configLoader.save(node);
            logger.info("Saved config");
        } catch (IOException | ObjectMappingException e) {
            logger.error("There was an error writing to the config file");
        }
    }
}