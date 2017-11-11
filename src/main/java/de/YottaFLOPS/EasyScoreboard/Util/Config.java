package de.YottaFLOPS.EasyScoreboard.Util;

import com.google.common.reflect.TypeToken;
import de.YottaFLOPS.EasyScoreboard.Main;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private static Logger logger;
    private static ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private static ConfigurationNode node;

    public static void init() {

        logger = LoggerFactory.getLogger("EasyScoreboard: Config");

        File config = new File(Main.normalConfig.toString());

        configLoader = HoconConfigurationLoader
                .builder()
                .setPath(Main.normalConfig)
                .build();

        if (!config.exists()) {
            logger.warn("Could not find config");


            InputStream stream = null;
            OutputStream resStreamOut = null;
            try {
                stream = Config.class.getResourceAsStream("/ExampleConfig.conf");
                int readBytes;
                byte[] buffer = new byte[4096];
                resStreamOut = new FileOutputStream(config.getAbsolutePath());
                while ((readBytes = stream.read(buffer)) > 0) {
                    resStreamOut.write(buffer, 0, readBytes);
                }
                logger.info("Created new config file");
            } catch (Exception e) {
                logger.error("There was an error creating the config file");
                logger.error("Please report the following lines on https://github.com/byYottaFLOPS/EasyScoreboards/issues");
                e.printStackTrace();
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (resStreamOut != null) {
                        resStreamOut.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void load() {
        try {
            node = configLoader.load();
            Main.scoreboardText.clear();
            List<? extends ConfigurationNode> nodeList = node.getNode("scoreboard", "lines").getChildrenList();
            for (ConfigurationNode n : nodeList) {
                String out = n.getValue().toString();
                String number = out.split(",")[0].split("=")[1];
                String text = out.split(",")[1].split("=")[1].replaceAll("}", "");
                Main.scoreboardText.add(new LineOfString(number, text));
            }

            Main.updateTicks = node.getNode("scoreboard").getNode("placeholderUpdateTicks").getInt();
            if (Main.updateTicks == 0) {
                Main.updateTicks = 20;
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
            e.printStackTrace();
        }
    }

    public static void save() {
        try {

            List<String> list = new ArrayList<>();

            for (LineOfString line : Main.scoreboardText) {
                list.add("number=" + line.getNumber() + ", text=" + line.getText());
            }

            node.getNode("scoreboard", "lines").setValue(new TypeToken<List<String>>() {}, list);

            node.getNode("scoreboard").getNode("showForAll").setValue(Main.showAll);
            node.getNode("scoreboard").getNode("placeholderUpdateTicks").setValue(Main.updateTicks);

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
        } catch (IOException | ObjectMappingException e) {
            logger.error("There was an error writing to the config file");
        }
    }
}