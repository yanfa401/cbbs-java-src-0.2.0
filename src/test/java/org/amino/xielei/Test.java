package org.amino.xielei;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.amino.ds.lockfree.LockFreeSet;

/**
 * @author xielei
 * @date 2022/11/14 16:54
 */
public class Test {

    private static final int THREAD_COUNT = 16;

    private static final int UNIT = 200000;

    private static final CountDownLatch LATCH = new CountDownLatch(THREAD_COUNT);

    public static void main(String[] args) throws InterruptedException {
        final Set<Long> set = new LockFreeSet<Long>(THREAD_COUNT * UNIT);
        //final Set<Long> set = new CopyOnWriteArraySet<Long>();
        long begin = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int start = (i) * UNIT;
            final int end = (i + 1) * UNIT;

            new Thread(new Runnable() {
                public void run() {
                    for (long j = start; j < end; j++) {
                        final long num = j;
                        set.add(num);
                    }
                    LATCH.countDown();
                }
            }).start();
        }

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        System.out.println(set.size());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        LATCH.await();
        System.out.println((System.currentTimeMillis() - begin)/1000 + "s");

        System.out.println(set.size());
    }

}
