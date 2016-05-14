package de.YottaFLOPS.EasyScoreboard;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

class setLine implements CommandExecutor {

    private final Main plugin;

    setLine(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(args.<Integer>getOne("Line").isPresent()) {
            int line = args.<Integer>getOne("Line").get();
            if (args.<String>getOne("New Text").isPresent()) {
                String newText = args.<String>getOne("New Text").get();
                Player player = (Player) Sponge.getServer().getOnlinePlayers().toArray()[0];

                if (newText.equals("")) {
                    newText = " ";
                }
                try {
                    if (line < 16 && line >= 0) {
                        if (plugin.replacePlaceholders(newText, player).length() < 30) {
                            plugin.scoreboardText[line] = newText;
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

                plugin.setBufferable(plugin.checkIfBufferable());
                plugin.usedPlayerCount = plugin.checkIfUsedPlayerCount();



                if (plugin.bufferable) {
                    plugin.bufferedScoreboard = plugin.makeScoreboard(player);
                    for (Player p : Sponge.getServer().getOnlinePlayers()) {
                        p.setScoreboard(plugin.bufferedScoreboard);
                    }
                } else {
                    for (Player p : Sponge.getServer().getOnlinePlayers()) {
                        p.setScoreboard(plugin.makeScoreboard(p));
                    }
                }

                plugin.handleConfig("save");
            } else {
                src.sendMessage(Text.of(TextColors.RED, "Missing text"));
            }
        } else {
            src.sendMessage(Text.of(TextColors.RED, "Missing line"));
        }
        return CommandResult.success();
    }
}
