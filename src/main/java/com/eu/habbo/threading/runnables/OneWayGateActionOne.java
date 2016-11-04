package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.util.pathfinding.PathFinder;

public class OneWayGateActionOne implements Runnable
{
    private HabboItem oneWayGate;
    private Room room;
    private GameClient client;

    public OneWayGateActionOne(GameClient client, Room room, HabboItem item)
    {
        this.oneWayGate = item;
        this.room = room;
        this.client = client;
    }

    @Override
    public void run()
    {
        this.room.sendComposer(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()).compose());

        RoomTile t = PathFinder.getSquareInFront(this.room.getLayout(), this.oneWayGate.getX(), this.oneWayGate.getY(), (this.oneWayGate.getRotation() + 4) % 8);
        
        if(client.getHabbo().getRoomUnit().animateWalk)
        {
            client.getHabbo().getRoomUnit().animateWalk = false;
        }
        
        if(this.room.tileWalkable(t) && this.client.getHabbo().getRoomUnit().getX() == this.oneWayGate.getX() && this.client.getHabbo().getRoomUnit().getY() == this.oneWayGate.getY())
        {
            this.client.getHabbo().getRoomUnit().setGoalLocation(t);
            
            if(!this.oneWayGate.getExtradata().equals("0"))
            {
                Emulator.getThreading().run(new HabboItemNewState(this.oneWayGate, this.room, "0"), 1000);
            }
        }
        else if(this.client.getHabbo().getRoomUnit().getX() == this.oneWayGate.getX() && this.client.getHabbo().getRoomUnit().getY() == this.oneWayGate.getY())
        {
            Emulator.getThreading().run(this, 500);
        }
        else 
        {
            if(!this.oneWayGate.getExtradata().equals("0"))
            {
                Emulator.getThreading().run(new HabboItemNewState(this.oneWayGate, this.room, "0"), 1000);
            }
        }
    }
}
