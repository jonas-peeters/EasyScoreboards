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

class countdownSet implements CommandExecutor {

    private final Main plugin;

    countdownSet(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if(commandContext.<Integer>getOne("Seconds").isPresent()) {
            if(commandContext.<String>getOne("Command").isPresent()) {
                plugin.countdownTime = commandContext.<Integer>getOne("Seconds").get();
                plugin.countdownTimeUse = commandContext.<Integer>getOne("Seconds").get();
                plugin.countdownCommand = commandContext.<String>getOne("Command").get().replaceAll("\"","'");
                commandSource.sendMessage(Text.of(TextColors.GRAY, "New Countdown Settings:"));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Time:            " + plugin.countdownTime + " seconds"));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Command:         " + plugin.countdownCommand));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Chat-Countdown:  " + plugin.countdownChat));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     XP-Countdown:    " + plugin.countdownXP));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Title-Countdown: " + plugin.countdownTitle));
                Player player = (Player) Sponge.getServer().getOnlinePlayers().toArray()[0];
                plugin.updateAllScoreboards(player);

                plugin.handleConfig("save");

            } else {
                commandSource.sendMessage(Text.of(TextColors.RED, "Could not find command"));
            }
        } else {
            commandSource.sendMessage(Text.of(TextColors.RED, "Could not find seconds"));
        }
        return CommandResult.success();
    }
}
