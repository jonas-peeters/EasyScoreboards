package de.YottaFLOPS.EasyScoreboard.Commands.Countdown;

import de.YottaFLOPS.EasyScoreboard.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

public class countdownStart implements CommandExecutor {

    private final Main plugin;

    public countdownStart(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        try {
            Main.countdownTask.cancel();
        } catch (Exception ignored) {}

        CommandManager commandManager = Sponge.getCommandManager();
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        CommandSource server = Sponge.getServer().getConsole();
        commandSource.sendMessage(Text.of(TextColors.GRAY, "Started countdown"));
        Main.countdownTask = taskBuilder.execute(new Runnable() {
            @Override
            public void run() {
                Main.countdownTimeUse--;
                if (Main.countdownTimeUse != 0) {
                    Player player = (Player) Sponge.getServer().getOnlinePlayers().toArray()[0];

                    if(Main.countdownChat) {
                        if (Main.countdownTimeUse > 3600) {
                            if (Main.countdownTimeUse % 3600 == 0) {
                                sendMessageToAllPlayers(Main.countdownTimeUse / 3600 + " hours left", TextColors.GREEN);
                            }
                        } else if (Main.countdownTimeUse > 600) {
                            if (Main.countdownTimeUse % 600 == 0) {
                                sendMessageToAllPlayers(Main.countdownTimeUse / 60 + " minutes left", TextColors.GREEN);
                            }
                        } else if (Main.countdownTimeUse > 60) {
                            if (Main.countdownTimeUse % 60 == 0) {
                                sendMessageToAllPlayers(Main.countdownTimeUse / 60 + " minutes left", TextColors.GREEN);
                            }
                        } else {
                            if (Main.countdownTimeUse == 5) {
                                sendMessageToAllPlayers(Main.countdownTimeUse + " seconds left", TextColors.YELLOW);
                            } else if (Main.countdownTimeUse == 4) {
                                sendMessageToAllPlayers(Main.countdownTimeUse + " seconds left", TextColors.YELLOW);
                            } else if (Main.countdownTimeUse == 3) {
                                sendMessageToAllPlayers(Main.countdownTimeUse + " seconds left", TextColors.GOLD);
                            } else if (Main.countdownTimeUse == 2) {
                                sendMessageToAllPlayers(Main.countdownTimeUse + " seconds left", TextColors.GOLD);
                            } else if (Main.countdownTimeUse == 1) {
                                sendMessageToAllPlayers(Main.countdownTimeUse + " seconds left", TextColors.RED);
                            } else if (Main.countdownTimeUse % 10 == 0) {
                                sendMessageToAllPlayers(Main.countdownTimeUse + " seconds left", TextColors.GREEN);
                            }
                        }
                    }
                    if(Main.countdownTitle) {
                        if (Main.countdownTimeUse > 3600) {
                            if (Main.countdownTimeUse % 3600 == 0) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, Main.countdownTimeUse / 3600 + " hours left")));
                            }
                        } else if (Main.countdownTimeUse > 600) {
                            if (Main.countdownTimeUse % 600 == 0) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, Main.countdownTimeUse / 60 + " minutes left")));
                            }
                        } else if (Main.countdownTimeUse > 60) {
                            if (Main.countdownTimeUse % 60 == 0) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, Main.countdownTimeUse / 60 + " minutes left")));
                            }
                        } else {
                            if (Main.countdownTimeUse == 5) {
                                player.sendTitle(Title.of(Text.of(TextColors.YELLOW, Main.countdownTimeUse + " seconds left")));
                                sendMessageToAllPlayers(Main.countdownTimeUse + " seconds left", TextColors.YELLOW);
                            } else if (Main.countdownTimeUse == 4) {
                                player.sendTitle(Title.of(Text.of(TextColors.YELLOW, Main.countdownTimeUse + " seconds left")));
                            } else if (Main.countdownTimeUse == 3) {
                                player.sendTitle(Title.of(Text.of(TextColors.GOLD, Main.countdownTimeUse + " seconds left")));
                            } else if (Main.countdownTimeUse == 2) {
                                player.sendTitle(Title.of(Text.of(TextColors.GOLD, Main.countdownTimeUse + " seconds left")));
                            } else if (Main.countdownTimeUse == 1) {
                                player.sendTitle(Title.of(Text.of(TextColors.RED, Main.countdownTimeUse + " seconds left")));
                            } else if (Main.countdownTimeUse % 10 == 0) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, Main.countdownTimeUse + " seconds left")));
                            }
                        }
                    }
                    if(Main.countdownXP) {
                        for(Player p : Sponge.getServer().getOnlinePlayers()) {
                            p.offer(Keys.EXPERIENCE_LEVEL, Main.countdownTimeUse);
                        }
                    }

                    plugin.updateAllScoreboards(player);

                } else {
                    if (!Main.countdownCommand.equals("")) {
                        commandManager.process(server, Main.countdownCommand.replaceAll("'", "\""));
                    }

                    if(Main.countdownXP) {
                        for(Player p : Sponge.getServer().getOnlinePlayers()) {
                            p.offer(Keys.EXPERIENCE_LEVEL, 0);
                        }
                    }

                    Main.countdownTimeUse = Main.countdownTime;

                    plugin.updateAllScoreboards((Player) Sponge.getServer().getOnlinePlayers().toArray()[0]);

                    Main.countdownTask.cancel();
                }

            }
        }).delayTicks(0).intervalTicks(20).submit(plugin);

        return CommandResult.success();
    }

    private void sendMessageToAllPlayers(String message, TextColor color) {
        for(Player p : Sponge.getServer().getOnlinePlayers()) {
            p.sendMessage(Text.of(color, message));
        }
    }
}
