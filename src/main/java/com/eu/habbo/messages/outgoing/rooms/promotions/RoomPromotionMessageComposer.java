package com.eu.habbo.messages.outgoing.rooms.promotions;

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

    @Override
    public ServerMessage compose()
    {

        this.response.init(Outgoing.RoomEventMessageComposer);

        if (room == null || roomPromotion == null)
        {
            // RoomID of the promoted room
            this.response.appendInt(-1);
            // UserID of the room owner
            this.response.appendInt(-1);
            // Username of the room owner
            this.response.appendString("");
            // Promoted status? Seems to only switch to 0 or 1
            this.response.appendInt(0);
            this.response.appendInt(0);
            // Promotion title
            this.response.appendString("");
            // Promotion description
            this.response.appendString("");
            // Category ID? Also seen in BuyRoomPromotionEvent (last int)
            this.response.appendInt(0);
            // On later production these are used to pass the time left in minutes
            this.response.appendInt(0);
            this.response.appendInt(0);
        }
        else
        {
            this.response.appendInt(room.getId());
            this.response.appendInt(room.getOwnerId());
            this.response.appendString(room.getOwnerName());

            this.response.appendInt(1);
            this.response.appendInt(1);

            this.response.appendString(roomPromotion.getTitle());
            this.response.appendString(roomPromotion.getDescription());
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendInt(0);
        }

        return this.response;

    }
}
