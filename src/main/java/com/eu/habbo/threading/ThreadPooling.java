package com.eu.habbo.threading;

import com.eu.habbo.Emulator;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.*;

public class ThreadPooling
{
    public final int threads;
    private final ScheduledExecutorService scheduledPool;
    private volatile boolean canAdd;

    public ThreadPooling(Integer threads)
    {
        this.threads = threads;
        this.scheduledPool = Executors.newScheduledThreadPool(this.threads, new DefaultThreadFactory("ArcturusThreadFactory"));
        this.canAdd = true;
        Emulator.getLogging().logStart("Thread Pool -> Loaded!");
    }

    public void run(Runnable run)
    {
        try
        {
            if (this.canAdd)
            {
                this.run(run, 0);
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
            if (this.canAdd)
            {
                this.scheduledPool.schedule(run, delay, TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public void shutDown()
    {
        this.canAdd = false;

        this.scheduledPool.shutdownNow();
        while(!this.scheduledPool.isTerminated()) {
        }
    }

    public ScheduledExecutorService getService()
    {
        return this.scheduledPool;
    }
}
