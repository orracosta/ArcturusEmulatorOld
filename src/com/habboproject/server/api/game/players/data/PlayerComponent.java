package com.habboproject.server.api.game.players.data;


import com.habboproject.server.api.game.players.BasePlayer;

public interface PlayerComponent {
    BasePlayer getPlayer();

    void dispose();
}
