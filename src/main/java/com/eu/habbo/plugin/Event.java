package com.eu.habbo.plugin;

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
