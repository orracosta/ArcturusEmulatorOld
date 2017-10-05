package com.habboproject.server.api.game.players.data.components.inventory;

import com.habboproject.server.api.game.furniture.types.FurnitureDefinition;
import com.habboproject.server.api.game.furniture.types.LimitedEditionItem;
import com.habboproject.server.api.networking.messages.IComposer;

public interface PlayerItem {
    long getId();

    FurnitureDefinition getDefinition();

    int getBaseId();

    int getGroupId();

    String getExtraData();

    LimitedEditionItem getLimitedEditionItem();

    int getVirtualId();

    void compose(IComposer message);

    PlayerItemSnapshot createSnapshot();
}