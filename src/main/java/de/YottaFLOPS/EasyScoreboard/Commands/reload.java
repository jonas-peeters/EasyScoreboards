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

public class reload implements CommandExecutor {

    private final Main plugin;

    public reload(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        Config.init();
        Config.load();

        if (commandSource instanceof Player) {
            plugin.updateAllScoreboards((Player) commandSource);
        } else if (Sponge.getServer().getOnlinePlayers().size() != 0) {
            plugin.updateAllScoreboards(Sponge.getServer().getOnlinePlayers().iterator().next());
        }

        TPS.stopTPS();

        if (Checks.checkIfUsedTPS(Main.scoreboardText)) {
            TPS.startTPS(plugin);
        }

        return CommandResult.success();
    }
}
