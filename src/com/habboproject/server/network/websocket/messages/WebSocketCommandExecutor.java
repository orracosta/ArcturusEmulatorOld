package com.habboproject.server.network.websocket.messages;

/**
 * Created by brend on 28/02/2017.
 */
public abstract class WebSocketCommandExecutor<T> {
    private final Class<T> type;

    public WebSocketCommandExecutor(Class<T> type) {
        this.type = type;
    }

    public abstract void handle(T instance);

    public Class<T> getType() {
        return type;
    }
}
