package de.YottaFLOPS.EasyScoreboard.Commands.Countdown;

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

public class CountdownAdd implements CommandExecutor {

    private final Main plugin;

    public CountdownAdd(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        if(commandContext.<Integer>getOne("seconds").isPresent()) {
            plugin.config.countdownTimeUse += commandContext.<Integer>getOne("seconds").get();

            if (Sponge.getServer().getOnlinePlayers().size() > 0) {
                plugin.updateAllScoreboards((Player) Sponge.getServer().getOnlinePlayers().toArray()[0]);
            }

            if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                commandSource.sendMessage(Text.of("Added "
                        + commandContext.<Integer>getOne("seconds").get() + "seconds to the countdown. This " +
                        "wont affect the time when you restart the countdown"));
            }
        } else {
            if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
                commandSource.sendMessage(Text.of(TextColors.RED, "Invalid time"));
            }
        }

        return CommandResult.success();
    }
}
