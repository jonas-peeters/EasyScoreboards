package de.YottaFLOPS.EasyScoreboard.Commands;

import de.YottaFLOPS.EasyScoreboard.Config;
import de.YottaFLOPS.EasyScoreboard.Main;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

class show implements CommandExecutor {

    private final Main plugin;

    public show(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {
            Main.dontShowFor.remove(src.getName());
            Config.save();
            plugin.setScoreboard((Player) src);
            src.sendMessage(Text.of(TextColors.GRAY, TextStyles.ITALIC, "Showing scoreboard"));
        }

        return CommandResult.success();
    }
}
