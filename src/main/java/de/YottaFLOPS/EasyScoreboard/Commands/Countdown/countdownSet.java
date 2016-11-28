package de.YottaFLOPS.EasyScoreboard.Commands.Countdown;

import de.YottaFLOPS.EasyScoreboard.Config;
import de.YottaFLOPS.EasyScoreboard.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class countdownSet implements CommandExecutor {

    private final Main plugin;

    public countdownSet(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if(commandContext.<Integer>getOne("Seconds").isPresent()) {
            if(commandContext.<String>getOne("Command").isPresent()) {
                Main.countdownTime = commandContext.<Integer>getOne("Seconds").get();
                Main.countdownTimeUse = commandContext.<Integer>getOne("Seconds").get();
                Main.countdownCommand = commandContext.<String>getOne("Command").get().replaceAll("\"","'");
                commandSource.sendMessage(Text.of(TextColors.GRAY, "New Countdown Settings:"));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Time:            " + Main.countdownTime + " seconds"));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Command:         " + Main.countdownCommand));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Chat-Countdown:  " + Main.countdownChat));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     XP-Countdown:    " + Main.countdownXP));
                commandSource.sendMessage(Text.of(TextColors.GRAY, "     Title-Countdown: " + Main.countdownTitle));
                Player player = (Player) Sponge.getServer().getOnlinePlayers().toArray()[0];
                plugin.updateAllScoreboards(player);

                Config.save();

            } else {
                commandSource.sendMessage(Text.of(TextColors.RED, "Could not find command"));
            }
        } else {
            commandSource.sendMessage(Text.of(TextColors.RED, "Could not find seconds"));
        }
        return CommandResult.success();
    }
}
