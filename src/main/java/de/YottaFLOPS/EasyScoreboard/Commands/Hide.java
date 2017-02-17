package de.YottaFLOPS.EasyScoreboard.Commands;

import de.YottaFLOPS.EasyScoreboard.Main;
import de.YottaFLOPS.EasyScoreboard.Utils.Config;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

class Hide implements CommandExecutor {

    private final Main plugin;

    public Hide(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext args) throws CommandException {

        if (commandSource instanceof Player) {
            Main.dontShowFor.add(commandSource.getName());
            Config.save();
            plugin.setScoreboard((Player) commandSource);
            commandSource.sendMessage(Text.of(TextColors.GRAY, TextStyles.ITALIC, "Hiding scoreboard"));
        }

        return CommandResult.success();
    }
}
