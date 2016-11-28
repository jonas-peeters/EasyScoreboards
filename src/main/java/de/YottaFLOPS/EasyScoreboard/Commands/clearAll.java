package de.YottaFLOPS.EasyScoreboard.Commands;

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

public class clearAll implements CommandExecutor {

    private final Main plugin;

    public clearAll(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Main.scoreboardText = new String[]{" "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "," "};
        src.sendMessage(Text.of(TextColors.GRAY, "Cleared scoreboard"));
        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            p.setScoreboard(plugin.makeScoreboard(p));
        }

        Config.save();

        plugin.usedPlayerCount = false;
        plugin.bufferable = true;

        return CommandResult.success();
    }
}
