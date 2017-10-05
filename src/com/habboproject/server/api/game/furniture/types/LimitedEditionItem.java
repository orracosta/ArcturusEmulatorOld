package com.habboproject.server.api.game.furniture.types;

public interface LimitedEditionItem {
    long getItemId();

    int getLimitedRare();

    int getLimitedRareTotal();
}
