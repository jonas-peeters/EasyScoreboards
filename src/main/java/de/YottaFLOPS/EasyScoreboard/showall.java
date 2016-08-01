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
import org.spongepowered.api.text.format.TextStyles;

class showall implements CommandExecutor {

    private final Main plugin;

    showall(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        plugin.showall = true;
        plugin.handleConfig("save");

        if(src instanceof Player) {
            plugin.updateAllScoreboards((Player) src);
            src.sendMessage(Text.of(TextColors.GRAY, TextStyles.ITALIC, "Showing scoreboard for all players"));
        } else {
            plugin.updateAllScoreboards((Player) Sponge.getServer().getOnlinePlayers().toArray()[0]);
        }

        plugin.stopTPS();
        if(plugin.checkIfUsedTPS()) {
            plugin.startTPS();
        }

        return CommandResult.success();
    }
}
