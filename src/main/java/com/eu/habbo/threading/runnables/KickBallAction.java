package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootball;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.messages.outgoing.rooms.items.ItemStateComposer;

public class KickBallAction implements Runnable
{
    private final InteractionFootball ball;
    private final Room room;
    private final RoomUnit kicker;
    private RoomUserRotation currentDirection;
    private int totalSteps;
    private int currentStep;
    public Boolean dead = false;

    public KickBallAction(InteractionFootball ball, Room room, RoomUnit kicker, RoomUserRotation direction, int steps)
    {
        this.ball = ball;
        this.room = room;
        this.kicker = kicker;
        this.currentDirection = direction;
        this.totalSteps = steps;
        this.currentStep = 0;
    }
    
    private Boolean validTile(Tile tile)
    {
        return validTile(tile.x, tile.y);
    }
    
    private Boolean validTile(int x, int y)
    {
        HabboItem topItem = room.getTopItemAt(x, y, this.ball);
        return room.tileWalkable(new Tile(x, y)) || !(topItem == null || topItem.getBaseItem().allowStack());
    }

    @Override
    public void run()
    {
        if(dead)
            return;
        
        if(currentStep < totalSteps)
        {            
            Tile next = PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), this.currentDirection.getValue());
            
            HabboItem topItem = room.getTopItemAt(this.ball.getX(), this.ball.getY(), this.ball);
            
            if (!validTile(next))
            {
                this.currentDirection = changeDirection(this.currentDirection);
                run();
            }
            else
            {
                //Move the ball & run again
                currentStep++; 
                
                float t = 2500; //Time for ball to finish it's cycle
                float delay = (totalSteps == 1) ? 500 : 100*((t=t/t-1)*t*t*t*t + 1) + (currentStep * 100); //Algorithm to work out the delay till next run
                
                this.ball.setExtradata(delay <= 200 ? "8" : (delay <= 250 ? "7" : (delay <= 300 ? "6" : (delay <= 350 ? "5" : (delay <= 400 ? "4" : (delay <= 450 ? "3" : (delay <= 500 ? "2" : "1")))))));
                this.room.sendComposer(new ItemStateComposer(this.ball).compose());
                
                next.Z = room.getStackHeight(next.X, next.Y, false);
                this.room.sendComposer(new FloorItemOnRollerComposer(this.ball, null, next, this.room).compose());                
                
                Emulator.getThreading().run(this, (long)delay);
            }
        }
        else
        {
            //We're done with the football. Stop it from rolling & end the thread.
            if(!this.ball.getExtradata().equals("0"))
            {
                this.ball.setExtradata("0");
                this.room.sendComposer(new ItemStateComposer(this.ball).compose());
                dead = true;
            }
        }
    }

    private RoomUserRotation changeDirection(RoomUserRotation direction)
    {
        switch(direction)
        {
            default:
            case NORTH:
                return RoomUserRotation.SOUTH;
            
            case NORTH_EAST:
                if(validTile(PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), RoomUserRotation.NORTH_WEST.getValue())))
                    return RoomUserRotation.NORTH_WEST;
                else if(validTile(PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), RoomUserRotation.SOUTH_EAST.getValue())))
                    return RoomUserRotation.SOUTH_EAST;
                else
                    return RoomUserRotation.SOUTH_WEST;
                
            case EAST:
                return RoomUserRotation.WEST;
                
            case SOUTH_EAST:
                if(validTile(PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), RoomUserRotation.SOUTH_WEST.getValue())))
                    return RoomUserRotation.SOUTH_WEST;
                else if(validTile(PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), RoomUserRotation.NORTH_EAST.getValue())))
                    return RoomUserRotation.NORTH_EAST;
                else
                    return RoomUserRotation.NORTH_WEST;
                
            case SOUTH:
                return RoomUserRotation.NORTH;
                
            case SOUTH_WEST:
                if(validTile(PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), RoomUserRotation.SOUTH_EAST.getValue())))
                    return RoomUserRotation.SOUTH_EAST;
                else if(validTile(PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), RoomUserRotation.SOUTH_EAST.getValue())))
                    return RoomUserRotation.NORTH_WEST;
                else
                    return RoomUserRotation.NORTH_EAST;
                
            case WEST:
                return RoomUserRotation.EAST;
                
            case NORTH_WEST:
                if(validTile(PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), RoomUserRotation.NORTH_EAST.getValue())))
                    return RoomUserRotation.NORTH_EAST;
                else if(validTile(PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), RoomUserRotation.SOUTH_WEST.getValue())))
                    return RoomUserRotation.SOUTH_WEST;
                else
                    return RoomUserRotation.SOUTH_EAST;
        }
    }
    
    public static RoomUserRotation oppositeDirection(RoomUserRotation direction)
    {
        switch(direction)
        {
            default:
            case NORTH:
                return RoomUserRotation.SOUTH;
            
            case NORTH_EAST:
                return RoomUserRotation.SOUTH_WEST;
                
            case EAST:
                return RoomUserRotation.WEST;
                
            case SOUTH_EAST:
                    return RoomUserRotation.NORTH_WEST;
                
            case SOUTH:
                return RoomUserRotation.NORTH;
                
            case SOUTH_WEST:
                    return RoomUserRotation.NORTH_EAST;
                
            case WEST:
                return RoomUserRotation.EAST;
                
            case NORTH_WEST:
                    return RoomUserRotation.SOUTH_EAST;
        }
    }
}
