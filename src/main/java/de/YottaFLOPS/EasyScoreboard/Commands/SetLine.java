package de.YottaFLOPS.EasyScoreboard.Commands;

import de.YottaFLOPS.EasyScoreboard.Main;
import de.YottaFLOPS.EasyScoreboard.Utils.Checks;
import de.YottaFLOPS.EasyScoreboard.Utils.Runnables;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

class SetLine implements CommandExecutor {

    private final Main plugin;

    public SetLine(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext args) throws CommandException {

        if(args.<Integer>getOne("LineOfString").isPresent()) {
            int line = args.<Integer>getOne("LineOfString").get();
            if (args.<String>getOne("New Text").isPresent()) {
                String newText = args.<String>getOne("New Text").get();
                plugin.setLine(newText, line, commandSource);

            } else {
                if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                    commandSource.sendMessage(Text.of(TextColors.RED, "Missing text"));
                }
            }
        } else {
            if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                commandSource.sendMessage(Text.of(TextColors.RED, "Missing line"));
            }
        }

        Runnables.stopTPS();
//        Runnables.stopMTime();
//        Runnables.stopSTime();
        Runnables.stopPlaceholderTask();
        if (Checks.checkIfUsedTPS(Main.scoreboardText)) {
            Runnables.startTPS(plugin);
        }
//        if (Checks.checkIfUsedMTime(Main.scoreboardText)) {
//            Runnables.startMTime(plugin);
//        }
//        if (Checks.checkIfUsedSTime(Main.scoreboardText)) {
//            Runnables.startSTime(plugin);
//        }
        if (Checks.checkIfUsedPlaceholders(Main.scoreboardText)) {
            Runnables.startPlaceholderTask(plugin);
        }

        return CommandResult.success();
    }
}
