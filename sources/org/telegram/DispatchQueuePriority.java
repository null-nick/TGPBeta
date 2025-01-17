package org.telegram;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.FileLog;

public class DispatchQueuePriority {
    private volatile CountDownLatch pauseLatch;
    ThreadPoolExecutor threadPoolExecutor;

    public static class PriorityRunnable implements Runnable {
        final int priority;
        final Runnable runnable;

        private PriorityRunnable(int i, Runnable runnable) {
            this.priority = i;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            this.runnable.run();
        }
    }

    public DispatchQueuePriority(String str) {
        int i = 1;
        this.threadPoolExecutor = new ThreadPoolExecutor(i, 1, 60L, TimeUnit.SECONDS, new PriorityBlockingQueue(10, new Comparator() {
            @Override
            public int compare(Runnable runnable, Runnable runnable2) {
                return (runnable2 instanceof PriorityRunnable ? ((PriorityRunnable) runnable2).priority : 1) - (runnable instanceof PriorityRunnable ? ((PriorityRunnable) runnable).priority : 1);
            }
        })) {
            @Override
            protected void beforeExecute(Thread thread, Runnable runnable) {
                CountDownLatch countDownLatch = DispatchQueuePriority.this.pauseLatch;
                if (countDownLatch != null) {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        FileLog.e(e);
                    }
                }
            }
        };
    }

    public void cancelRunnable(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        this.threadPoolExecutor.remove(runnable);
    }

    public void pause() {
        if (this.pauseLatch == null) {
            this.pauseLatch = new CountDownLatch(1);
        }
    }

    public Runnable postRunnable(Runnable runnable, int i) {
        if (i != 1) {
            runnable = new PriorityRunnable(i, runnable);
        }
        postRunnable(runnable);
        return runnable;
    }

    public void postRunnable(Runnable runnable) {
        this.threadPoolExecutor.execute(runnable);
    }

    public void resume() {
        CountDownLatch countDownLatch = this.pauseLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
            this.pauseLatch = null;
        }
    }
}
