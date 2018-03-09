package de.YottaFLOPS.EasyScoreboard.Commands;

import de.YottaFLOPS.EasyScoreboard.Commands.Countdown.*;
import de.YottaFLOPS.EasyScoreboard.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Register {
    public static void registerCommands(Main main) {
        HashMap<List<String>, CommandSpec> subcommands = new HashMap<>();
        HashMap<List<String>, CommandSpec> subcommandsCountdown = new HashMap<>();

        subcommandsCountdown.put(Collections.singletonList("set"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.set")
                .description(Text.of("Set the countdown time in seconds"))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("Seconds"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("Command"))))
                .executor(new CountdownSet(main))
                .build());

        subcommandsCountdown.put(Collections.singletonList("add"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.add")
                .description(Text.of("Add time to the current countdown in seconds"))
                .arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("seconds"))))
                .executor(new CountdownAdd(main))
                .build());

        subcommandsCountdown.put(Collections.singletonList("start"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.start")
                .description(Text.of("Starts the countdown"))
                .executor(new CountdownStart(main))
                .build());

        subcommandsCountdown.put(Collections.singletonList("stop"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.stop")
                .description(Text.of("Stops the countdown"))
                .executor(new CountdownStop(main))
                .build());

        subcommandsCountdown.put(Collections.singletonList("reset"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.reset")
                .description(Text.of("Resets the countdown"))
                .executor(new CountdownReset(main))
                .build());

        subcommandsCountdown.put(Collections.singletonList("xp"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.xp")
                .description(Text.of("Set if there should be a XP countdown"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("true/false"))))
                .executor(new CountdownXP(main))
                .build());

        subcommandsCountdown.put(Collections.singletonList("chat"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.chat")
                .description(Text.of("Set if there should be a chat countdown"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("true/false"))))
                .executor(new CountdownChat(main))
                .build());

        subcommandsCountdown.put(Collections.singletonList("title"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.title")
                .description(Text.of("Set if there should be a title countdown"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("true/false"))))
                .executor(new CountdownTitle(main))
                .build());

        subcommands.put(Collections.singletonList("clear"), CommandSpec.builder()
                .permission("easyscoreboard.clear")
                .description(Text.of("Clear the complete scoreboard"))
                .executor(new ClearAll(main))
                .build());

        subcommands.put(Collections.singletonList("Reload"), CommandSpec.builder()
                .permission("easyscoreboard.Reload")
                .description(Text.of("Reload esb config"))
                .executor(new Reload(main))
                .build());

        subcommands.put(Collections.singletonList("countdown"), CommandSpec.builder()
                .permission("easyscoreboard.countdown.use")
                .description(Text.of("Edit the countdown"))
                .children(subcommandsCountdown)
                .build());

        subcommands.put(Collections.singletonList("Show"), CommandSpec.builder()
                .permission("easyscoreboard.Show")
                .description(Text.of("Enable scoreboard"))
                .executor(new Show(main))
                .build());

        subcommands.put(Collections.singletonList("Hide"), CommandSpec.builder()
                .permission("easyscoreboard.Hide")
                .description(Text.of("Hide scoreboard"))
                .executor(new Hide(main))
                .build());

        subcommands.put(Collections.singletonList("showAll"), CommandSpec.builder()
                .permission("easyscoreboard.showAll")
                .description(Text.of("Enable scoreboard for all players (not with private settings)"))
                .executor(new ShowAll(main))
                .build());

        subcommands.put(Collections.singletonList("hideAll"), CommandSpec.builder()
                .permission("easyscoreboard.HideAll")
                .description(Text.of("Disable scoreboard for all players (also with private settings)"))
                .executor(new HideAll(main))
                .build());

        CommandSpec easyscoreboardsCommandSpec = CommandSpec.builder()
                .extendedDescription(Text.of("Scoreboard Commands"))
                .permission("easyscoreboard.use")
                .children(subcommands)
                .build();

        Sponge.getCommandManager().register(main, easyscoreboardsCommandSpec, "easyscoreboard");
        Sponge.getCommandManager().register(main, easyscoreboardsCommandSpec, "esb");
    }
}
