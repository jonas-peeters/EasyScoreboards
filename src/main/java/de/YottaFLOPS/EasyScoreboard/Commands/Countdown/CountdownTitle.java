package de.YottaFLOPS.EasyScoreboard.Commands.Countdown;

import de.YottaFLOPS.EasyScoreboard.Util.Config;
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

public class CountdownTitle implements CommandExecutor {

    private final Main plugin;

    public CountdownTitle(Main instance) { plugin = instance; }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        if(commandContext.<Boolean>getOne("true/false").isPresent()) {
            plugin.config.countdownTitle = commandContext.<Boolean>getOne("true/false").get();
            if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                commandSource.sendMessage(Text.of(TextColors.GRAY, "New Countdown Settings:"));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Time:            " + plugin.config.countdownTime + " seconds"));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Command:         " + plugin.config.countdownCommand));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Chat-Countdown:  " + plugin.config.countdownChat));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     XP-Countdown:    " + plugin.config.countdownXP));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Title-Countdown: " + plugin.config.countdownTitle));
            }
        } else {
            if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                commandSource.sendMessage(Text.of(TextColors.RED, "This argument is not valid"));
            }
        }

        plugin.config.save();

        return CommandResult.success();
    }
}
