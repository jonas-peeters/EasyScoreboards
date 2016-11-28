package de.YottaFLOPS.EasyScoreboard.Commands;

import de.YottaFLOPS.EasyScoreboard.Checks;
import de.YottaFLOPS.EasyScoreboard.Config;
import de.YottaFLOPS.EasyScoreboard.Main;
import de.YottaFLOPS.EasyScoreboard.TPS;
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

public class showall implements CommandExecutor {

    private final Main plugin;

    public showall(Main instance) {
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

        TPS.stopTPS();
        if(Checks.checkIfUsedTPS(Main.scoreboardText)) {
            TPS.startTPS(plugin);
        }

        return CommandResult.success();
    }
}
