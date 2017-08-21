package com.waitsober.stopwatch;

import org.springframework.util.StopWatch;

/**
 * Created by sober on 2017/5/9.
 *
 * @author sober
 * @date 2017/05/09
 */
public class StopWatchUtils {

    static ThreadLocal<StopWatch> stopWatchThreadLocal = ThreadLocal.withInitial(() -> new StopWatch("stopwatch"));



    public static StopWatch getCurrentStopWatch() {
        StopWatch stopWatch = stopWatchThreadLocal.get();
        if (stopWatch == null) {
            stopWatch = new StopWatch("stopwatch");
            stopWatchThreadLocal.set(stopWatch);
        }
        return stopWatch;
    }
    /**
     * start
     */
    public static void start(String taskName) {
        if (!getCurrentStopWatch().isRunning()) {
            getCurrentStopWatch().start();
        } else {
            getCurrentStopWatch().stop();
            getCurrentStopWatch().start(taskName);
        }
    }

    /**
     * stop
     */
    public static void stop() {
        if (getCurrentStopWatch().isRunning()) {
            getCurrentStopWatch().stop();
        }
    }

    public static String prettyPrint() {
        return getCurrentStopWatch().prettyPrint();
    }


    public static void removeStopWatch() {
        stopWatchThreadLocal.remove();
    }

}
