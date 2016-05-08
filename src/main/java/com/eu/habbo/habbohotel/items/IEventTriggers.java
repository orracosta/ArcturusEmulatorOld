package com.eu.habbo.habbohotel.items;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;

/**
 * Created on 9-10-2014 17:15.
 */
public interface IEventTriggers
{
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception;

    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception;

    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception;
}
