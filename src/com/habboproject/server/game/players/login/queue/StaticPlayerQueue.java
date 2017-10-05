package com.habboproject.server.game.players.login.queue;

import com.habboproject.server.threads.ThreadManager;


public class StaticPlayerQueue {
    private static PlayerLoginQueueManager mgr;

    static {

    }

    public static void init(ThreadManager threadManagement) {
        mgr = new PlayerLoginQueueManager(true, threadManagement);
    }

    public static PlayerLoginQueueManager getQueueManager() {
        return mgr;
    }
}
