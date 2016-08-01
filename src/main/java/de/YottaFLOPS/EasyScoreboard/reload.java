package de.YottaFLOPS.EasyScoreboard;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

class reload implements CommandExecutor {

    private final Main plugin;

    reload(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        plugin.handleConfig("init");
        plugin.handleConfig("load");

        plugin.updateAllScoreboards((Player) commandSource);

        plugin.stopTPS();
        if(plugin.checkIfUsedTPS()) {
            plugin.startTPS();
        }

        return CommandResult.success();
    }
}
