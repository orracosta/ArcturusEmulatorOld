package com.eu.habbo.habbohotel.items.interactions.games.football;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.threading.runnables.KickBallAction;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Rotation;
import com.eu.habbo.util.pathfinding.Tile;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFootball extends InteractionDefault
{
    public InteractionFootball(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionFootball(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }
    
    /*@Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return true;
    }*/
    
    private KickBallAction currentThread;
    
    @Override
    public void onWalkOff(RoomUnit roomUnit, final Room room, Object[] objects) throws Exception
    {
        if(!(currentThread == null || currentThread.dead))
            return;
        
        int length = 6;
        
        if(this.getBaseItem().getName().equals("bw_bball"))
        {
            length = 4;
        }
        
        if(length > 0)
        {
            if(currentThread != null)
            {
                currentThread.dead = true;
            }
            currentThread = new KickBallAction(this, room, roomUnit, KickBallAction.oppositeDirection(RoomUserRotation.values()[Rotation.Calculate(this.getX(), this.getY(), roomUnit.getGoalX(), roomUnit.getGoalY())]), length);
            Emulator.getThreading().run(currentThread, 0);
        }
    }
    
    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(PathFinder.tilesAdjecent(new Tile(client.getHabbo().getRoomUnit().getX(), client.getHabbo().getRoomUnit().getY()), new Tile(this.getX(), this.getY())))
        {
            if(currentThread != null)
            {
                currentThread.dead = true;
            }
            currentThread = new KickBallAction(this, room, client.getHabbo().getRoomUnit(), client.getHabbo().getRoomUnit().getBodyRotation(), 2);
            Emulator.getThreading().run(currentThread, 0);
        }
    }
    
    @Override
    public void onWalkOn(RoomUnit roomUnit, final Room room, Object[] objects) throws Exception
    {        
        int length = 0;
        
        if(roomUnit.getPathFinder().getPath().isEmpty() && roomUnit.tilesWalked() == 2)
            return;
        
        if(this.getX() == roomUnit.getGoalX() && this.getY() == roomUnit.getGoalY())
        {
            length = 6;
            if(this.getBaseItem().getName().equals("bw_bball"))
            {
                length = 4;
            }
        }
        else
        {            
            length = 1;
        }
        
        if(currentThread != null)
        {
            currentThread.dead = true;
        }
        currentThread = new KickBallAction(this, room, roomUnit, roomUnit.getBodyRotation(), length);
        Emulator.getThreading().run(currentThread, 0);
    }
}
