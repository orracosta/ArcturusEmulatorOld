package com.habboproject.server.network.messages.outgoing.room.items;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class SendFloorItemMessageComposer extends MessageComposer {
    private final RoomItemFloor itemFloor;

    public SendFloorItemMessageComposer(RoomItemFloor itemFloor) {
        this.itemFloor = itemFloor;
    }

    @Override
    public short getId() {
        return Composers.ObjectAddMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        this.itemFloor.serialize(msg, true);
    }
}
