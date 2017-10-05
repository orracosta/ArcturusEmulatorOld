package com.habboproject.server.api.networking.sessions;

public class SessionManagerAccessor {
    private static SessionManagerAccessor instance;

    private ISessionManager sessionManager;

    public ISessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(ISessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public static SessionManagerAccessor getInstance() {
        if (instance == null) {
            instance = new SessionManagerAccessor();
        }

        return instance;
    }

}
