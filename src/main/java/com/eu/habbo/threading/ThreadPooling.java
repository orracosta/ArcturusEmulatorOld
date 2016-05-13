package com.eu.habbo.threading;

import com.eu.habbo.Emulator;

import java.util.concurrent.*;

public class ThreadPooling
{
    private final ThreadPoolExecutor threadPool;
    private final ScheduledExecutorService scheduledPool;

    private static volatile boolean canAdd;

    public ThreadPooling(Integer threads)
    {
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(3, 10, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory, rejectionHandler);
        this.scheduledPool = Executors.newScheduledThreadPool(2);
        canAdd = true;
        Emulator.getLogging().logStart("Thread Pool -> Loaded!");
    }

    public void run(Runnable run)
    {
        try
        {
            if (canAdd)
            {
                this.threadPool.execute(run);
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public void run(Runnable run, long delay)
    {
        try
        {
            if (canAdd)
            {
                this.scheduledPool.schedule(run, delay, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
            Emulator.getLogging().logErrorLine(e.getCause());
        }
    }

    public void shutDown()
    {
        canAdd = false;
        this.threadPool.shutdownNow();
        while (!this.threadPool.isTerminated()) {
        }

        this.scheduledPool.shutdownNow();
        while(!this.scheduledPool.isTerminated()) {
        }
    }
}
