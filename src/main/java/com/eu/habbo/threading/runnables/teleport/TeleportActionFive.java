package com.eu.habbo.threading.runnables.teleport;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleportTile;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.threading.runnables.HabboItemNewState;
import com.eu.habbo.util.pathfinding.PathFinder;

class TeleportActionFive implements Runnable
{
    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    public TeleportActionFive(HabboItem currentTeleport, Room room, GameClient client)
    {
        this.currentTeleport = currentTeleport;
        this.client = client;
        this.room = room;
    }

    @Override
    public void run()
    {
        this.client.getHabbo().getRoomUnit().isTeleporting = false;

        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != this.room)
            return;

        if (!(this.currentTeleport instanceof InteractionTeleportTile))
        {
            this.client.getHabbo().getRoomUnit().setGoalLocation(PathFinder.getSquareInFront(this.room.getLayout(), this.currentTeleport.getX(), this.currentTeleport.getY(), this.currentTeleport.getRotation()));
        }

        Emulator.getThreading().run(new HabboItemNewState(this.currentTeleport, room, "0"), 1000);
    }
}
