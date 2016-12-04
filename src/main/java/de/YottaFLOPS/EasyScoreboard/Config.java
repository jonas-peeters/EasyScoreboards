package de.YottaFLOPS.EasyScoreboard;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Config {

    private static Logger logger;
    private static ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private static ConfigurationNode node;

    public static void init() {

        logger = LoggerFactory.getLogger("EasyScoreboard: Config");

        File config = new File(Main.normalConfig.toString());

        if (!config.exists()) {
            logger.warn("Could not find config");
            try {
                //noinspection ResultOfMethodCallIgnored
                config.createNewFile();
                logger.info("Created config file");
            } catch (IOException e) {
                logger.error("There was an error creating the config file");
            }

            configLoader = HoconConfigurationLoader.builder().setPath(Main.normalConfig).build();
            node = configLoader.createEmptyNode(ConfigurationOptions.defaults());

            ConfigurationNode newNode = node.getNode("scoreboard").getNode("line");
            newNode.getNode("0").setValue("GREENBOLDHi; GOLDBOLDPLAYERNAME:");
            newNode.getNode("1").setValue("GREENMain command:");
            newNode.getNode("2").setValue("    /esb");
            newNode.getNode("3").setValue("----------------------");
            newNode.getNode("4").setValue("GREENEdit line:");
            newNode.getNode("5").setValue("    /esb set <Line> <Text>");
            newNode.getNode("6").setValue("----------------------");
            newNode.getNode("7").setValue("GREENStart countdown:");
            newNode.getNode("8").setValue("    /esb countdown start");
            newNode.getNode("9").setValue("");
            newNode.getNode("10").setValue("");
            newNode.getNode("11").setValue("");
            newNode.getNode("12").setValue("");
            newNode.getNode("13").setValue("");
            newNode.getNode("14").setValue("");
            newNode.getNode("15").setValue("");

            node.getNode("scoreboard").getNode("hideFor").setValue(" ");
            node.getNode("scoreboard").getNode("showForAll").setValue(true);
            node.getNode("scoreboard").getNode("countdown").getNode("time").setValue(11);
            node.getNode("scoreboard").getNode("countdown").getNode("command").setValue("say The countdown is over");
            node.getNode("scoreboard").getNode("countdown").getNode("chat").setValue(true);
            node.getNode("scoreboard").getNode("countdown").getNode("xp").setValue(false);
            node.getNode("scoreboard").getNode("countdown").getNode("title").setValue(true);

            try {
                configLoader.save(node);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            configLoader = HoconConfigurationLoader.builder().setPath(Main.normalConfig).build();
            node = configLoader.createEmptyNode(ConfigurationOptions.defaults());
        }
    }

    public static void load() {
        try {
            node = configLoader.load();

            for(int i = 0; i < 16; i++) {
                Main.scoreboardText[i] = node.getNode("scoreboard", "line", String.valueOf(i)).getString();
            }

            Main.showAll = node.getNode("scoreboard").getNode("showForAll").getBoolean();
            Main.countdownTime = node.getNode("scoreboard").getNode("countdown").getNode("time").getInt();
            Main.countdownCommand = node.getNode("scoreboard").getNode("countdown").getNode("command").getString();
            Main.countdownChat = node.getNode("scoreboard").getNode("countdown").getNode("chat").getBoolean();
            Main.countdownXP = node.getNode("scoreboard").getNode("countdown").getNode("xp").getBoolean();
            Main.countdownTitle = node.getNode("scoreboard").getNode("countdown").getNode("title").getBoolean();
            Main.countdownTimeUse = Main.countdownTime;

            String hideFor = node.getNode("scoreboard").getNode("hideFor").getString();
            String[] hideForSplit = hideFor.split(" ");

            Main.dontShowFor.clear();
            for(String s : hideForSplit) {
                Main.dontShowFor.add(s.replace(" ",""));
            }

            logger.info("Loaded config");
        } catch (Exception e) {
            logger.error("There was an error reading the config file");
        }
    }

    public static void save() {
        try {
            for(int i = 0; i < 16; i++) {
                node.getNode("scoreboard", "line", String.valueOf(i)).setValue(Main.scoreboardText[i]);
            }

            node.getNode("scoreboard").getNode("showForAll").setValue(Main.showAll);

            node.getNode("scoreboard").getNode("countdown").getNode("time").setValue(Main.countdownTime);
            node.getNode("scoreboard").getNode("countdown").getNode("command").setValue(Main.countdownCommand);
            node.getNode("scoreboard").getNode("countdown").getNode("chat").setValue(Main.countdownChat);
            node.getNode("scoreboard").getNode("countdown").getNode("xp").setValue(Main.countdownXP);
            node.getNode("scoreboard").getNode("countdown").getNode("title").setValue(Main.countdownTitle);

            String hideFor = "";

            for(String s : Main.dontShowFor) {
                hideFor = hideFor + " " + s;
            }

            node.getNode("scoreboard").getNode("hideFor").setValue(hideFor);

            configLoader.save(node);
            logger.info("Saved config");
        } catch (IOException e) {
            logger.error("There was an error writing to the config file");
        }
    }
}