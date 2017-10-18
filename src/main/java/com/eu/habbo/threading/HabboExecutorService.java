package com.eu.habbo.threading;

import com.eu.habbo.Emulator;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class HabboExecutorService extends ScheduledThreadPoolExecutor
{
    public HabboExecutorService(int corePoolSize, ThreadFactory threadFactory)
    {
        super(corePoolSize, threadFactory);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t)
    {
        super.afterExecute(r, t);

        if (t != null && !(t instanceof IOException))
        {
            try
            {
                Emulator.getLogging().logErrorLine(t);
            }
            catch (Exception e)
            {}
        }
    }
}
