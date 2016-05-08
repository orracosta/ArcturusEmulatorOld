package com.eu.habbo.plugin;

/**
 * Created on 28-2-2015 18:49.
 */
public abstract class Event
{
    private boolean cancelled = false;

    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public boolean isCancelled()
    {
        return this.cancelled;
    }
}
