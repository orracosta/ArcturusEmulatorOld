package com.habboproject.server.api.game.furniture.types;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItemSnapshot;

public interface SongItem {

    int getSongId();

    PlayerItemSnapshot getItemSnapshot();
}
