package com.habboproject.server.api.networking.sessions;

import com.habboproject.server.api.game.players.BasePlayer;
import com.habboproject.server.api.networking.messages.IMessageComposer;

public interface BaseSession {
    BasePlayer getPlayer();

    void disconnect();

    BaseSession send(IMessageComposer messageComposer);

    BaseSession sendQueue(IMessageComposer messageComposer);

    String getIpAddress();
}
