package de.YottaFLOPS.EasyScoreboard.Util;

import de.YottaFLOPS.EasyScoreboard.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

public class Runnables {

    private static Task TPSTask1;
    private static Task TPSTask2;
//    private static Task STime;
//    private static Task MTime;
    private static Task PlaceholderAPI;
    public static double lastTPS = 20.0;
    private static double tps = 0.0;

    //Starting the runnable to update the tps count
    public static void startTPS(Main plugin) {
        TPSTask1 = Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if(tps/10 != lastTPS) {
                lastTPS = tps/10;
                if(Sponge.getServer().getOnlinePlayers().size() != 0) {
                    plugin.updateAllScoreboards(Sponge.getServer().getOnlinePlayers().iterator().next());
                }
            }
            tps = 0;
        }).intervalTicks(200).delayTicks(200).submit(plugin);

        TPSTask2 = Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if(Sponge.getServer().getOnlinePlayers().size() != 0) {
                tps += Sponge.getServer().getTicksPerSecond();
            } else {
                tps += 20.0;
            }
        }).intervalTicks(20).submit(plugin);
    }

    //Stopping the runnable to update the tps count
    public static void stopTPS() {
        tps = 0.0;
        if(TPSTask1 != null) {
            TPSTask1.cancel();
        }
        if(TPSTask2 != null) {
            TPSTask2.cancel();
        }
    }

    //Starting the runnable to update the placeholder api placeholders
    public static void startPlaceholderTask(Main plugin) {
        PlaceholderAPI = Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if (Sponge.getServer().getOnlinePlayers().size() != 0) {
                plugin.updateAllScoreboards(Sponge.getServer().getOnlinePlayers().iterator().next());
            }
        }).intervalTicks(plugin.config.updateTicks).delayTicks(plugin.config.updateTicks).submit(plugin);
    }

    //Stopping the runnable to update the placeholder api placeholders
    public static void stopPlaceholderTask() {
        if(PlaceholderAPI != null) {
            PlaceholderAPI.cancel();
        }
    }
}
