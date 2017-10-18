package com.eu.habbo.threading;

import com.eu.habbo.Emulator;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler
{

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
    {
        Emulator.getLogging().logErrorLine(r.toString() + " is rejected");
    }
}
