package de.YottaFLOPS.EasyScoreboard.Commands;

import de.YottaFLOPS.EasyScoreboard.Utils.Checks;
import de.YottaFLOPS.EasyScoreboard.Utils.Config;
import de.YottaFLOPS.EasyScoreboard.Main;
import de.YottaFLOPS.EasyScoreboard.Utils.Runnables;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

class ShowAll implements CommandExecutor {

    private final Main plugin;

    public ShowAll(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Main.showAll = true;
        Config.save();

        if(src instanceof Player) {
            plugin.updateAllScoreboards((Player) src);
            src.sendMessage(Text.of(TextColors.GRAY, TextStyles.ITALIC, "Showing scoreboard for all players"));
        } else {
            plugin.updateAllScoreboards((Player) Sponge.getServer().getOnlinePlayers().toArray()[0]);
        }

        Runnables.stopTPS();
//        Runnables.stopMTime();
//        Runnables.stopSTime();
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
