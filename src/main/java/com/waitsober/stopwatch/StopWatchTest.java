package com.waitsober.stopwatch;

import org.apache.log4j.Logger;

/**
 * Created by sober on 2017/5/9.
 *
 * @author sober
 * @date 2017/05/09
 */
public class StopWatchTest {

    private static final Logger logger = Logger.getLogger(StopWatchTest.class);

    public static void main(String[] args) {
        TestDemo();
    }


     public static void TestDemo(){
        StopWatchUtils.start("task1");
        for (int i = 0;i<100; i++) {
            System.out.println("123131313131");
         }
        StopWatchUtils.stop();
        logger.info(StopWatchUtils.prettyPrint());
        StopWatchUtils.removeStopWatch();

    }
}
