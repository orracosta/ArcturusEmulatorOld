package com.habboproject.server.network.messages.outgoing.room.items;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class SendWallItemMessageComposer extends MessageComposer {
    private final RoomItemWall itemWall;

    public SendWallItemMessageComposer(RoomItemWall itemWall) {
        this.itemWall = itemWall;
    }

    @Override
    public short getId() {
        return Composers.ItemAddMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        this.itemWall.serialize(msg);
    }
}
