package com.habboproject.server.network.messages.outgoing.room.events;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.game.rooms.types.RoomPromotion;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RoomPromotionMessageComposer extends MessageComposer {
    private final RoomData roomData;
    private final RoomPromotion roomPromotion;

    public RoomPromotionMessageComposer(final RoomData roomData, final RoomPromotion roomPromotion) {
        this.roomData = roomData;
        this.roomPromotion = roomPromotion;
    }

    @Override
    public short getId() {
        return Composers.RoomEventMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        if (roomData == null || roomPromotion == null) {
            msg.writeInt(-1);
            msg.writeInt(-1);
            msg.writeString("");
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeString("");
            msg.writeString("");
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            return;
        }

        msg.writeInt(roomData.getId());
        msg.writeInt(roomData.getOwnerId());
        msg.writeString(roomData.getOwner());

        msg.writeInt(1);
        msg.writeInt(1);

        msg.writeString(roomPromotion.getPromotionName());
        msg.writeString(roomPromotion.getPromotionDescription());
        msg.writeInt(0);

        msg.writeInt((int) ((roomPromotion.getTimestampFinish() - roomPromotion.getTimestampStart()) / 60));
        msg.writeInt(0);

    }
}
