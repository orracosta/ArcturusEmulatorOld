package com.habboproject.server.network.messages.outgoing.user.inventory;

import com.habboproject.server.api.game.furniture.types.SongItem;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;

public class SongInventoryMessageComposer extends MessageComposer {

    private List<SongItem> songItems;

    public SongInventoryMessageComposer(List<SongItem> songItems) {
        this.songItems = songItems;
    }

    @Override
    public short getId() {
        return Composers.SongInventoryMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.songItems.size());

        for (SongItem songItem : this.songItems) {
            msg.writeInt(ItemManager.getInstance().getItemVirtualId(songItem.getItemSnapshot().getId()));
            msg.writeInt(songItem.getSongId());
        }
    }
}
