package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionPushable;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;

public class KickBallAction implements Runnable
{
    private final InteractionPushable ball; //The item which is moving
    private final Room room; //The room that the item belongs to
    private final RoomUnit kicker; //The Habbo which initiated the move of the item
    private RoomUserRotation currentDirection; //The current direction the item is moving in
    private final int totalSteps; //The total number of steps in the move sequence
    private int currentStep; //The current step of the move sequence
    public boolean dead = false; //When true the run() function will not execute. Used when another user kicks the ball whilst it is arleady moving.

    public KickBallAction(InteractionPushable ball, Room room, RoomUnit kicker, RoomUserRotation direction, int steps)
    {
        this.ball = ball;
        this.room = room;
        this.kicker = kicker;
        this.currentDirection = direction;
        this.totalSteps = steps;
        this.currentStep = 0;
    }

    @Override
    public void run()
    {
        if(dead || !this.room.isLoaded())
            return;
        
        if(currentStep < totalSteps)
        {            
            Tile next = PathFinder.getSquareInFront(this.ball.getX(), this.ball.getY(), this.currentDirection.getValue());            
            if (!this.ball.validMove(this.room, new Tile(this.ball.getX(), this.ball.getY()), next))
            {
                RoomUserRotation oldDirection = this.currentDirection;
                this.currentDirection = this.ball.getBounceDirection(this.room, this.currentDirection);
                if(this.currentDirection != oldDirection)
                {
                    this.ball.onBounce(room, oldDirection, this.currentDirection, this.kicker);
                }
                else
                {
                    currentStep = totalSteps; //End the move sequence, the ball can't bounce anywhere
                }
                run();
            }
            else
            {
                //Move the ball & run again
                currentStep++;
                
                int delay = this.ball.getNextRollDelay(this.currentStep, this.totalSteps); //Algorithm to work out the delay till next run
                
                if(this.ball.canStillMove(room, new Tile(this.ball.getX(), this.ball.getY()), next, this.currentDirection, this.kicker, delay, this.currentStep, this.totalSteps))
                {
                    this.ball.onMove(room, new Tile(this.ball.getX(), this.ball.getY()), next, this.currentDirection, this.kicker, delay, this.currentStep, this.totalSteps);
                
                    next.Z = room.getStackHeight(next.X, next.Y, false);
                    this.room.sendComposer(new FloorItemOnRollerComposer(this.ball, null, next, this.room).compose());                

                    Emulator.getThreading().run(this, (long)delay);
                }
                else
                {
                    currentStep = totalSteps; //End the move sequence, the ball can't bounce anywhere
                    run();
                }
            }
        }
        else
        {
            //We're done with the move sequence. Stop it the sequence & end the thread.
            this.ball.onStop(room, this.kicker, this.currentStep, this.totalSteps);
            dead = true;
        }
    }
}
