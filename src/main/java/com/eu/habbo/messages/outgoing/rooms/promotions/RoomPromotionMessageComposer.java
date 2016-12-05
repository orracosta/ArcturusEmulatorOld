package com.eu.habbo.messages.outgoing.rooms.promotions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomPromotion;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomPromotionMessageComposer extends MessageComposer
{
    private final Room room;
    private final RoomPromotion roomPromotion;

    public RoomPromotionMessageComposer(Room room, RoomPromotion roomPromotion)
    {
        this.room = room;
        this.roomPromotion = roomPromotion;
    }

    /*
     * Secondary constructor to avoid NPE when a room is not promoted, roomPromotion filled with default data
     * Only call this constructor when you know a RoomPromotion object is going to be null
     */
    public RoomPromotionMessageComposer(Room room)
    {
        this.room = room;
        this.roomPromotion = new RoomPromotion(room, "", "", 0);
    }

    @Override
    public ServerMessage compose()
    {

        this.response.init(Outgoing.RoomEventMessageComposer);

        if (roomPromotion.getEndTimestamp() == 0)
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
