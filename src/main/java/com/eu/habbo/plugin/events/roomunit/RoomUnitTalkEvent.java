package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.plugin.events.roomunit.RoomUnitEvent;

/**
 * Created on 11-6-2015 15:59.
 */
public class RoomUnitTalkEvent extends RoomUnitEvent
{
    public final RoomChatMessage message;

    RoomUnitTalkEvent(RoomUnit roomUnit, RoomChatMessage message)
    {
        super(roomUnit);
        
        this.message = message;
    }
}
