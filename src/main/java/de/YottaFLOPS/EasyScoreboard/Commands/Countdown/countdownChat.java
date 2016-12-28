package de.YottaFLOPS.EasyScoreboard.Commands.Countdown;

import de.YottaFLOPS.EasyScoreboard.Config;
import de.YottaFLOPS.EasyScoreboard.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class countdownChat implements CommandExecutor {

    public countdownChat() {}

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        if(commandContext.<Boolean>getOne("true/false").isPresent()) {
            Main.countdownChat = commandContext.<Boolean>getOne("true/false").get();
            commandSource.sendMessage(Text.of(TextColors.GRAY, "New Countdown Settings:"));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     Time:            " + Main.countdownTime + " seconds"));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     Command:         " + Main.countdownCommand));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     Chat-Countdown:  " + Main.countdownChat));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     XP-Countdown:    " + Main.countdownXP));
            commandSource.sendMessage(Text.of(TextColors.GRAY, "     Title-Countdown: " + Main.countdownTitle));
        } else {
            commandSource.sendMessage(Text.of(TextColors.RED, "This argument is not valid"));
        }

        Config.save();

        return CommandResult.success();
    }
}
