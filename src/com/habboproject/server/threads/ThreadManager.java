package com.habboproject.server.threads;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.utilities.Initializable;
import org.apache.log4j.Logger;

import java.util.concurrent.*;


public class ThreadManager implements Initializable {
    private static ThreadManager threadManagerInstance;

    public static int POOL_SIZE = 0;
    private ScheduledExecutorService scheduledExecutorService;

    public ThreadManager() {

    }

    public static ThreadManager getInstance() {
        if (threadManagerInstance == null)
            threadManagerInstance = new ThreadManager();

        return threadManagerInstance;
    }

    @Override
    public void initialize() {
        int poolSize = Integer.parseInt((String) Comet.getServer().getConfig().getOrDefault("comet.system.threads", "8"));

        this.scheduledExecutorService = Executors.newScheduledThreadPool(poolSize, r -> {
            POOL_SIZE++;

            Thread scheduledThread = new Thread(r);
            scheduledThread.setName("Comet-Scheduler-Thread-" + POOL_SIZE);

            final Logger log = Logger.getLogger("Comet-Scheduler-Thread-" + POOL_SIZE);
            scheduledThread.setUncaughtExceptionHandler((t, e) -> log.error("Exception in Comet Worker Thread", e));

            return scheduledThread;
        });
    }

    public void execute(CometThread task) {
        try {
            this.scheduledExecutorService.execute(task);
        } catch (Exception e) {

        }
    }

    public Object execute(Callable<?> task) throws ExecutionException, InterruptedException {
        return this.scheduledExecutorService.submit(task).get();
    }

    public ScheduledFuture executePeriodic(CometThread task, long initialDelay, long period, TimeUnit unit) {
        return this.scheduledExecutorService.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public ScheduledFuture executePeriodicWithFixedDelay(CometThread task, long initialDelay, long period, TimeUnit unit) {
        return this.scheduledExecutorService.scheduleWithFixedDelay(task, initialDelay, period, unit);
    }

    public ScheduledFuture executeSchedule(CometThread task, long delay, TimeUnit unit) {
        return this.scheduledExecutorService.schedule(task, delay, unit);
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
}