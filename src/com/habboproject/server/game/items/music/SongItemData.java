package com.habboproject.server.game.items.music;

import com.habboproject.server.api.game.furniture.types.SongItem;
import com.habboproject.server.api.game.players.data.components.inventory.PlayerItemSnapshot;
import com.habboproject.server.game.players.components.types.inventory.InventoryItemSnapshot;

public class SongItemData implements SongItem {

    private InventoryItemSnapshot itemSnapshot;
    private int songId;

    public SongItemData(InventoryItemSnapshot itemSnapshot, int songId) {
        this.itemSnapshot = itemSnapshot;
        this.songId = songId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public PlayerItemSnapshot getItemSnapshot() {
        return itemSnapshot;
    }
}
