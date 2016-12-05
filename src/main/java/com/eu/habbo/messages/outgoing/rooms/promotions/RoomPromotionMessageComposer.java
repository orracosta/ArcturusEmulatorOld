package com.eu.habbo.messages.outgoing.rooms.promotions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomPromotion;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.sun.istack.internal.Nullable;

public class RoomPromotionMessageComposer extends MessageComposer
{
    private final Room room;
    private final RoomPromotion roomPromotion;

    public RoomPromotionMessageComposer(@Nullable Room room, @Nullable RoomPromotion roomPromotion)
    {
        this.room = room;
        this.roomPromotion = roomPromotion;
    }

    @Override
    public ServerMessage compose()
    {

        this.response.init(Outgoing.RoomEventMessageComposer);

        if (room == null || roomPromotion == null)
        {
            this.response.appendInt32(-1);
            this.response.appendInt32(-1);
            this.response.appendString("");
            this.response.appendInt32(0);
            this.response.appendInt32(0);
            this.response.appendString("");
            this.response.appendString("");
            this.response.appendInt32(0);
            this.response.appendInt32(0);
            this.response.appendInt32(0);
        }

        this.response.appendInt32(room.getId());
        this.response.appendInt32(room.getOwnerId());
        this.response.appendString(room.getOwnerName());

        this.response.appendInt32(1);
        this.response.appendInt32(1);

        this.response.appendString(roomPromotion.getTitle());
        this.response.appendString(roomPromotion.getDescription());
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);

        return this.response;

    }
}
