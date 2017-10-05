package com.habboproject.server.api.server;

import com.habboproject.server.api.events.EventHandler;
import com.habboproject.server.api.networking.sessions.ISessionManager;

public interface IGameService {
    ISessionManager getSessionManager();

    EventHandler getEventHandler();
}
