package de.YottaFLOPS.EasyScoreboard;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

public class Runnables {

    private static Task TPSTask1;
    private static Task TPSTask2;
    private static Task STime;
    private static Task MTime;
    static double lastTPS = 20.0;
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
        if(TPSTask1 != null) {
            TPSTask1.cancel();
        }
        if(TPSTask2 != null) {
            TPSTask2.cancel();
        }
    }

    //Starting the runnable to update the minecraft time
    public static void startMTime(Main plugin) {
        MTime = Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if (Sponge.getServer().getOnlinePlayers().size() != 0) {
                plugin.updateAllScoreboards(Sponge.getServer().getOnlinePlayers().iterator().next());
            }
        }).intervalTicks(167).delayTicks(167).submit(plugin);
    }

    //Stopping the runnable to update the minecraft time
    public static void stopMTime() {
        if(MTime != null) {
            MTime.cancel();
        }
    }

    //Starting the runnable to update the real time
    public static void startSTime(Main plugin) {
        STime = Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if (Sponge.getServer().getOnlinePlayers().size() != 0) {
                plugin.updateAllScoreboards(Sponge.getServer().getOnlinePlayers().iterator().next());
            }
        }).intervalTicks(1200).delayTicks(167).submit(plugin);
    }

    //Stopping the runnable to update the real time
    public static void stopSTime() {
        if(STime != null) {
            STime.cancel();
        }
    }
}
