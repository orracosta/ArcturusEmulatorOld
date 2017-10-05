package com.habboproject.server.api.networking.sessions;

import com.habboproject.server.api.networking.messages.IMessageComposer;

import java.util.List;
import java.util.Map;

public interface ISessionManager {
    boolean disconnectByPlayerId(int id);

    BaseSession getByPlayerId(int id);

    List<BaseSession> getPlayersByRank(int rank);

    BaseSession getByPlayerUsername(String username);

    int getUsersOnlineCount();

    Map<Integer, BaseSession> getSessions();

    void broadcast(IMessageComposer msg);

    void broadcastToModerators(IMessageComposer messageComposer);
}
