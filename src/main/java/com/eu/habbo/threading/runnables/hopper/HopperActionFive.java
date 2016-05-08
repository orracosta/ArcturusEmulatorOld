package com.eu.habbo.threading.runnables.hopper;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.threading.runnables.HabboItemNewState;
import com.eu.habbo.util.pathfinding.PathFinder;

/**
 * Created on 8-3-2015 11:08.
 */
class HopperActionFive implements Runnable
{
    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    public HopperActionFive(HabboItem currentTeleport, Room room, GameClient client)
    {
        this.currentTeleport = currentTeleport;
        this.client = client;
        this.room = room;
    }

    @Override
    public void run()
    {
        this.client.getHabbo().getRoomUnit().isTeleporting = false;
        this.client.getHabbo().getRoomUnit().setGoalLocation(PathFinder.getSquareInFront(this.currentTeleport.getX(), this.currentTeleport.getY(), this.currentTeleport.getRotation()));

        Emulator.getThreading().run(new HabboItemNewState(this.currentTeleport, this.room, "0"), 1000);
    }
}
