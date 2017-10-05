package com.habboproject.server.modules;

import com.habboproject.server.api.events.EventHandler;
import com.habboproject.server.api.networking.sessions.ISessionManager;
import com.habboproject.server.api.server.IGameService;
import com.habboproject.server.network.NetworkManager;

public class CometGameService implements IGameService {
    /**
     * The main system-wide event handler
     */
    private EventHandler eventHandler;

    /**
     * Default constructor
     */
    public CometGameService(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * Get the main event handler
     *
     * @return EventHandler instance
     */
    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public ISessionManager getSessionManager() {
        return NetworkManager.getInstance().getSessions();
    }
}
