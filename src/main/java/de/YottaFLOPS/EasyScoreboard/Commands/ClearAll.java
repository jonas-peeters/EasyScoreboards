package de.YottaFLOPS.EasyScoreboard.Commands;

import de.YottaFLOPS.EasyScoreboard.Util.Config;
import de.YottaFLOPS.EasyScoreboard.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;

class ClearAll implements CommandExecutor {

    private final Main plugin;

    ClearAll(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext args) throws CommandException {
        plugin.config.scoreboardText = new ArrayList<>();
        if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
            commandSource.sendMessage(Text.of(TextColors.GRAY, "Cleared scoreboard"));
        }
        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            p.setScoreboard(plugin.makeScoreboard(p));
        }

        plugin.config.save();

        plugin.config.usedPlaceholders = true;

        return CommandResult.success();
    }
}
