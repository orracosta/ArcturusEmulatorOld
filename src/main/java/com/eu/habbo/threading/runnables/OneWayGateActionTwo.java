package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.util.pathfinding.PathFinder;

/**
 * Created on 10-3-2015 10:01.
 */
class OneWayGateActionTwo implements Runnable
{
    private final HabboItem oneWayGate;
    private final Room room;
    private final GameClient client;

    public OneWayGateActionTwo(GameClient client, Room room, HabboItem item)
    {
        this.oneWayGate = item;
        this.room = room;
        this.client = client;
    }

    @Override
    public void run()
    {

    }
}
