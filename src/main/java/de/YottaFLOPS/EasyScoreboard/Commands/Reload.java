package de.YottaFLOPS.EasyScoreboard.Commands;

import de.YottaFLOPS.EasyScoreboard.Main;
import de.YottaFLOPS.EasyScoreboard.Util.Config;
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

class Reload implements CommandExecutor {

    private final Main plugin;

    public Reload(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext args) throws CommandException {

        try {
            plugin.reload();
        } catch (Exception ignored) {}

        if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
            commandSource.sendMessage(Text.of(TextColors.GRAY, "EasyScoreboard reloaded config successfully"));
        }

        return CommandResult.success();
    }
}
