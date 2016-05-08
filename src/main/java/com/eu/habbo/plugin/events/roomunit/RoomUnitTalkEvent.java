package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.plugin.events.roomunit.RoomUnitEvent;

public class RoomUnitTalkEvent extends RoomUnitEvent
{
    public final RoomChatMessage message;

    public RoomUnitTalkEvent(RoomUnit roomUnit, RoomChatMessage message)
    {
        super(roomUnit);
        
        this.message = message;
    }
}
