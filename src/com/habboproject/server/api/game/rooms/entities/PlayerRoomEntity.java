package com.habboproject.server.api.game.rooms.entities;

import com.habboproject.server.api.game.players.BasePlayer;

/**
 * Created by brend on 13/03/2017.
 */
public interface PlayerRoomEntity {
    int getId();

    BasePlayer getPlayer();
}
