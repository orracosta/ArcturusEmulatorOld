package com.habboproject.server.api.events;

public abstract class EventArgs {

    private boolean cancelled = false;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return false;
    }

}
