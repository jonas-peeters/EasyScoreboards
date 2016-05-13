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

class setLine implements CommandExecutor {

    private final Main plugin;

    setLine(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        int line = 0;
        try {
            line = Integer.valueOf(args.<Integer>getOne("Line").toString().replace("Optional[", "").replace("]", ""));
        } catch (Exception ignored) {}
        String newText = args.<String>getOne("New Text").toString().replace("Optional[", "").replace("]", "");
        if(newText.equals("")) {
            newText = " ";
        }
        try {
            if(line < 16 && line >= 0) {
                if(plugin.removeStyleAndColor(newText).length() <= 38) {
                    plugin.scoreboardText[line] = newText;
                    src.sendMessage(Text.of(TextColors.DARK_GRAY, "Setting line " + line + " to: " + newText));
                } else {
                    src.sendMessage(Text.of(TextColors.RED, "The lenght of one line is limited to 38!"));
                }
            } else {
                src.sendMessage(Text.of(TextColors.RED, "You may only use lines between 0 and 15!"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            p.setScoreboard(plugin.makeScoreboard(p));
        }
        plugin.handleConfig(new String[]{"save"});
        return CommandResult.success();
    }
}
