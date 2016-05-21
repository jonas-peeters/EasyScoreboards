package de.YottaFLOPS.EasyScoreboard;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

class countdownTitle implements CommandExecutor {

    private final Main plugin;

    countdownTitle(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        if(commandContext.<Boolean>getOne("true/false").isPresent()) {
            plugin.countdownTitle = commandContext.<Boolean>getOne("true/false").get();
            commandSource.sendMessage(Text.of(TextColors.GRAY, "New Countdown Settings:"));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     Time:            " + plugin.countdownTime + " seconds"));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     Command:         " + plugin.countdownCommand));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     Chat-Countdown:  " + plugin.countdownChat));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     XP-Countdown:    " + plugin.countdownXP));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     Title-Countdown: " + plugin.countdownTitle));
        } else {
            commandSource.sendMessage(Text.of(TextColors.RED, "This argument is not valid"));
        }

        plugin.handleConfig("save");

        return CommandResult.success();
    }
}
