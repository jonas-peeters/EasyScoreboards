package de.YottaFLOPS.EasyScoreboard.Commands.Countdown;

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

public class CountdownSet implements CommandExecutor {

    private final Main plugin;

    public CountdownSet(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if(commandContext.<Integer>getOne("Seconds").isPresent()) {
            if(commandContext.<String>getOne("Command").isPresent()) {
                plugin.config.countdownTime = commandContext.<Integer>getOne("Seconds").get();
                plugin.config.countdownTimeUse = commandContext.<Integer>getOne("Seconds").get();
                plugin.config.countdownCommand = commandContext.<String>getOne("Command").get().replaceAll("\"","'");
                if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                    commandSource.sendMessage(Text.of(TextColors.GRAY, "New Countdown Settings:"));
                    commandSource.sendMessage(Text.of(TextColors.GRAY, "     Time:            " + plugin.config.countdownTime + " seconds"));
                    commandSource.sendMessage(Text.of(TextColors.GRAY, "     Command:         " + plugin.config.countdownCommand));
                    commandSource.sendMessage(Text.of(TextColors.GRAY, "     Chat-Countdown:  " + plugin.config.countdownChat));
                    commandSource.sendMessage(Text.of(TextColors.GRAY, "     XP-Countdown:    " + plugin.config.countdownXP));
                    commandSource.sendMessage(Text.of(TextColors.GRAY, "     Title-Countdown: " + plugin.config.countdownTitle));
                }
                if (Sponge.getServer().getOnlinePlayers().size() > 0) {
                    Player player = (Player) Sponge.getServer().getOnlinePlayers().toArray()[0];
                    plugin.updateAllScoreboards(player);
                }

                plugin.config.save();

            } else {
                if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                    commandSource.sendMessage(Text.of(TextColors.RED, "Could not find command"));
                }
            }
        } else {
            if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                commandSource.sendMessage(Text.of(TextColors.RED, "Could not find seconds"));
            }
        }
        return CommandResult.success();
    }
}
