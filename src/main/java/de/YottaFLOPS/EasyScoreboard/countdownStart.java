package de.YottaFLOPS.EasyScoreboard;

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

class countdownStart implements CommandExecutor {

    private final Main plugin;

    countdownStart(Main instance) {
        plugin = instance;
    }

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {

        try {
            plugin.countdownTask.cancel();
        } catch (Exception ignored) {}

        CommandManager commandManager = Sponge.getCommandManager();
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        CommandSource server = Sponge.getServer().getConsole();
        commandSource.sendMessage(Text.of(TextColors.GRAY, "Started countdown"));
        plugin.countdownTask = taskBuilder.execute(new Runnable() {
            @Override
            public void run() {
                plugin.countdownTimeUse--;
                if (plugin.countdownTimeUse != 0) {
                    Player player = (Player) Sponge.getServer().getOnlinePlayers().toArray()[0];

                    if(plugin.countdownChat) {
                        if (plugin.countdownTimeUse > 3600) {
                            if (plugin.countdownTimeUse % 3600 == 0) {
                                sendMessageToAllPlayers(plugin.countdownTimeUse / 3600 + " hours left", TextColors.GREEN);
                            }
                        } else if (plugin.countdownTimeUse > 600) {
                            if (plugin.countdownTimeUse % 600 == 0) {
                                sendMessageToAllPlayers(plugin.countdownTimeUse / 60 + " minutes left", TextColors.GREEN);
                            }
                        } else if (plugin.countdownTimeUse > 60) {
                            if (plugin.countdownTimeUse % 60 == 0) {
                                sendMessageToAllPlayers(plugin.countdownTimeUse / 60 + " minutes left", TextColors.GREEN);
                            }
                        } else {
                            if (plugin.countdownTimeUse == 5) {
                                sendMessageToAllPlayers(plugin.countdownTimeUse + " seconds left", TextColors.YELLOW);
                            } else if (plugin.countdownTimeUse == 4) {
                                sendMessageToAllPlayers(plugin.countdownTimeUse + " seconds left", TextColors.YELLOW);
                            } else if (plugin.countdownTimeUse == 3) {
                                sendMessageToAllPlayers(plugin.countdownTimeUse + " seconds left", TextColors.GOLD);
                            } else if (plugin.countdownTimeUse == 2) {
                                sendMessageToAllPlayers(plugin.countdownTimeUse + " seconds left", TextColors.GOLD);
                            } else if (plugin.countdownTimeUse == 1) {
                                sendMessageToAllPlayers(plugin.countdownTimeUse + " seconds left", TextColors.RED);
                            } else if (plugin.countdownTimeUse % 10 == 0) {
                                sendMessageToAllPlayers(plugin.countdownTimeUse + " seconds left", TextColors.GREEN);
                            }
                        }
                    }
                    if(plugin.countdownTitle) {
                        if (plugin.countdownTimeUse > 3600) {
                            if (plugin.countdownTimeUse % 3600 == 0) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, plugin.countdownTimeUse / 3600 + " hours left")));
                            }
                        } else if (plugin.countdownTimeUse > 600) {
                            if (plugin.countdownTimeUse % 600 == 0) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, plugin.countdownTimeUse / 60 + " minutes left")));
                            }
                        } else if (plugin.countdownTimeUse > 60) {
                            if (plugin.countdownTimeUse % 60 == 0) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, plugin.countdownTimeUse / 60 + " minutes left")));
                            }
                        } else {
                            if (plugin.countdownTimeUse == 5) {
                                player.sendTitle(Title.of(Text.of(TextColors.YELLOW, plugin.countdownTimeUse + " seconds left")));
                                sendMessageToAllPlayers(plugin.countdownTimeUse + " seconds left", TextColors.YELLOW);
                            } else if (plugin.countdownTimeUse == 4) {
                                player.sendTitle(Title.of(Text.of(TextColors.YELLOW, plugin.countdownTimeUse + " seconds left")));
                            } else if (plugin.countdownTimeUse == 3) {
                                player.sendTitle(Title.of(Text.of(TextColors.GOLD, plugin.countdownTimeUse + " seconds left")));
                            } else if (plugin.countdownTimeUse == 2) {
                                player.sendTitle(Title.of(Text.of(TextColors.GOLD, plugin.countdownTimeUse + " seconds left")));
                            } else if (plugin.countdownTimeUse == 1) {
                                player.sendTitle(Title.of(Text.of(TextColors.RED, plugin.countdownTimeUse + " seconds left")));
                            } else if (plugin.countdownTimeUse % 10 == 0) {
                                player.sendTitle(Title.of(Text.of(TextColors.GREEN, plugin.countdownTimeUse + " seconds left")));
                            }
                        }
                    }
                    if(plugin.countdownXP) {
                        for(Player p : Sponge.getServer().getOnlinePlayers()) {
                            p.offer(Keys.EXPERIENCE_LEVEL, plugin.countdownTimeUse);
                        }
                    }

                    plugin.updateAllScoreboards(player);

                } else {
                    if (!plugin.countdownCommand.equals("")) {
                        commandManager.process(server, plugin.countdownCommand.replaceAll("'", "\""));
                    }

                    if(plugin.countdownXP) {
                        for(Player p : Sponge.getServer().getOnlinePlayers()) {
                            p.offer(Keys.EXPERIENCE_LEVEL, 0);
                        }
                    }

                    plugin.countdownTimeUse = plugin.countdownTime;

                    plugin.updateAllScoreboards((Player) Sponge.getServer().getOnlinePlayers().toArray()[0]);

                    plugin.countdownTask.cancel();
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
