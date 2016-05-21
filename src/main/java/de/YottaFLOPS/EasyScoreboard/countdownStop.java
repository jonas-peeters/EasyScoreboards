package de.YottaFLOPS.EasyScoreboard;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

class countdownStop implements CommandExecutor {

    private final Main plugin;

    countdownStop(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        plugin.countdownTask.cancel();
        commandSource.sendMessage(Text.of(TextColors.GRAY, "The countdown has been stopped"));

        return CommandResult.success();
    }
}
