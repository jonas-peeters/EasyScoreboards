package de.YottaFLOPS.EasyScoreboard.Commands.Countdown;

import de.YottaFLOPS.EasyScoreboard.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

public class CountdownStart implements CommandExecutor {

    private final Main plugin;

    public CountdownStart(Main instance) {
        plugin = instance;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        try {
            plugin.countdownTask.cancel();
        } catch (Exception ignored) {}

        CommandManager commandManager = Sponge.getCommandManager();
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        CommandSource server = Sponge.getServer().getConsole();
        if (commandSource instanceof Player || commandSource instanceof ConsoleSource) {
            commandSource.sendMessage(Text.of(TextColors.GRAY, "Started countdown"));
        }
        plugin.countdownTask = taskBuilder.execute(() -> {
            plugin.config.countdownTimeUse--;
            if (plugin.config.countdownTimeUse != 0) {
                if(plugin.config.countdownChat) {
                    if (plugin.config.countdownTimeUse > 3600) {
                        if (plugin.config.countdownTimeUse % 3600 == 0) {
                            sendMessageToAllPlayers(plugin.config.countdownTimeUse / 3600 + " hours left", TextColors.GREEN);
                        }
                    } else if (plugin.config.countdownTimeUse > 600) {
                        if (plugin.config.countdownTimeUse % 600 == 0) {
                            sendMessageToAllPlayers(plugin.config.countdownTimeUse / 60 + " minutes left", TextColors.GREEN);
                        }
                    } else if (plugin.config.countdownTimeUse > 60) {
                        if (plugin.config.countdownTimeUse % 60 == 0) {
                            sendMessageToAllPlayers(plugin.config.countdownTimeUse / 60 + " minutes left", TextColors.GREEN);
                        }
                    } else {
                        if (plugin.config.countdownTimeUse == 5) {
                            sendMessageToAllPlayers(plugin.config.countdownTimeUse + " seconds left", TextColors.YELLOW);
                        } else if (plugin.config.countdownTimeUse == 4) {
                            sendMessageToAllPlayers(plugin.config.countdownTimeUse + " seconds left", TextColors.YELLOW);
                        } else if (plugin.config.countdownTimeUse == 3) {
                            sendMessageToAllPlayers(plugin.config.countdownTimeUse + " seconds left", TextColors.GOLD);
                        } else if (plugin.config.countdownTimeUse == 2) {
                            sendMessageToAllPlayers(plugin.config.countdownTimeUse + " seconds left", TextColors.GOLD);
                        } else if (plugin.config.countdownTimeUse == 1) {
                            sendMessageToAllPlayers(plugin.config.countdownTimeUse + " second left", TextColors.RED);
                        } else if (plugin.config.countdownTimeUse % 10 == 0) {
                            sendMessageToAllPlayers(plugin.config.countdownTimeUse + " seconds left", TextColors.GREEN);
                        }
                    }
                }
                if(plugin.config.countdownTitle) {
                    if (plugin.config.countdownTimeUse > 3600) {
                        if (plugin.config.countdownTimeUse % 3600 == 0) {
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, plugin.config.countdownTimeUse / 3600 + " hours left")));
                            }
                        }
                    } else if (plugin.config.countdownTimeUse > 600) {
                        if (plugin.config.countdownTimeUse % 600 == 0) {
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, plugin.config.countdownTimeUse / 60 + " minutes left")));
                            }
                        }
                    } else if (plugin.config.countdownTimeUse > 60) {
                        if (plugin.config.countdownTimeUse % 60 == 0) {
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, plugin.config.countdownTimeUse / 60 + " minutes left")));
                            }
                        }
                    } else {
                        if (plugin.config.countdownTimeUse == 5) {
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                player.sendTitle(Title.of(Text.of(TextColors.YELLOW, plugin.config.countdownTimeUse + " seconds left")));
                            }
                        } else if (plugin.config.countdownTimeUse == 4) {
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                player.sendTitle(Title.of(Text.of(TextColors.YELLOW, plugin.config.countdownTimeUse + " seconds left")));
                            }
                        } else if (plugin.config.countdownTimeUse == 3) {
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                player.sendTitle(Title.of(Text.of(TextColors.GOLD, plugin.config.countdownTimeUse + " seconds left")));
                            }
                        } else if (plugin.config.countdownTimeUse == 2) {
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                player.sendTitle(Title.of(Text.of(TextColors.GOLD, plugin.config.countdownTimeUse + " seconds left")));
                            }
                        } else if (plugin.config.countdownTimeUse == 1) {
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                player.sendTitle(Title.of(Text.of(TextColors.RED, plugin.config.countdownTimeUse + " second left")));
                            }
                        } else if (plugin.config.countdownTimeUse % 10 == 0) {
                            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, plugin.config.countdownTimeUse + " seconds left")));
                            }
                        }
                    }
                }
                if(plugin.config.countdownXP) {
                    for(Player p : Sponge.getServer().getOnlinePlayers()) {
                        p.offer(Keys.EXPERIENCE_LEVEL, plugin.config.countdownTimeUse);
                    }
                }
                if (Sponge.getServer().getOnlinePlayers().size() > 0) {
                    plugin.updateAllScoreboards((Player) Sponge.getServer().getOnlinePlayers().toArray()[0]);
                }

            } else {
                if (!plugin.config.countdownCommand.equals("")) {
                    commandManager.process(server, plugin.config.countdownCommand.replaceAll("'", "\""));
                }

                if(plugin.config.countdownXP) {
                    for(Player p : Sponge.getServer().getOnlinePlayers()) {
                        p.offer(Keys.EXPERIENCE_LEVEL, 0);
                    }
                }

                plugin.config.countdownTimeUse = plugin.config.countdownTime;

                if (Sponge.getServer().getOnlinePlayers().size() > 0) {
                    plugin.updateAllScoreboards((Player) Sponge.getServer().getOnlinePlayers().toArray()[0]);
                }

                plugin.countdownTask.cancel();
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
