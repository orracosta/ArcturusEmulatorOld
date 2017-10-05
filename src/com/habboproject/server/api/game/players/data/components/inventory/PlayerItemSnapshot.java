package com.habboproject.server.api.game.players.data.components.inventory;

public interface PlayerItemSnapshot {
   long getId();

   int getBaseItemId();

   String getExtraData();
}
