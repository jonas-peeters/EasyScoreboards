package de.YottaFLOPS.EasyScoreboard.Commands.Countdown;

import de.YottaFLOPS.EasyScoreboard.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CountdownStop implements CommandExecutor {

    private final Main plugin;

    public CountdownStop(Main instance) { plugin = instance; }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        plugin.countdownTask.cancel();
        if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
            commandSource.sendMessage(Text.of(TextColors.GRAY, "The countdown has been stopped"));
        }

        return CommandResult.success();
    }
}
