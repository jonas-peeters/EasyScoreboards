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

class clearAll implements CommandExecutor {

    private final Main plugin;

    clearAll(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        plugin.scoreboardText = new String[]{" "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "};
        src.sendMessage(Text.of(TextColors.DARK_GRAY, "Cleared scoreboard"));
        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            p.setScoreboard(plugin.makeScoreboard(p));
        }
        plugin.handleConfig(new String[]{"save"});
        return CommandResult.success();
    }
}
