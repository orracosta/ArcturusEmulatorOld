package com.habboproject.server.api.events;

import java.util.function.Consumer;

public abstract class Event<T extends EventArgs> {

    private final Consumer<T> callback;

    public Event(Consumer<T> callback) {
        this.callback = callback;
    }

    public void consume(T args) {
        this.callback.accept(args);
    }

    public boolean isAsync() {
        return false;
    }
}
