package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.threading.runnables.KickBallAction;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionPushable extends InteractionDefault
{
    /**
     * Currently running Runnable for this item.
     */
    private KickBallAction currentThread;
    
    public InteractionPushable(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionPushable(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }
    
    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return true;
    }
    
    @Override
    public void onWalkOff(RoomUnit roomUnit, final Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);
        
        if(!(currentThread == null || currentThread.dead))
        {
            currentThread = null;
            return;
        }
        
        int velocity = this.getWalkOffVelocity(roomUnit, room);
        RoomUserRotation direction = this.getWalkOffDirection(roomUnit, room);
        this.onKick(room, roomUnit, velocity, direction);
        
        if(velocity > 0)
        {
            if(currentThread != null)
                currentThread.dead = true;
            
            currentThread = new KickBallAction(this, room, roomUnit, direction, velocity);
            Emulator.getThreading().run(currentThread, 0);
        }
    }
    
    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);
        
        if(RoomLayout.tilesAdjecent(client.getHabbo().getRoomUnit().getCurrentLocation(), room.getLayout().getTile(this.getX(), this.getY())))
        {
            int velocity = this.getTackleVelocity(client.getHabbo().getRoomUnit(), room);
            RoomUserRotation direction = this.getWalkOnDirection(client.getHabbo().getRoomUnit(), room);
            this.onTackle(room, client.getHabbo().getRoomUnit(), velocity, direction);
            
            if(velocity > 0)
            {
                if(currentThread != null)
                    currentThread.dead = true;
                
                currentThread = new KickBallAction(this, room, client.getHabbo().getRoomUnit(), direction, velocity);
                Emulator.getThreading().run(currentThread, 0);
            }
        }
    }
    
    @Override
    public void onWalkOn(RoomUnit roomUnit, final Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);
        
        int velocity;
        RoomUserRotation direction;
        
        if(this.getX() == roomUnit.getGoal().x && this.getY() == roomUnit.getGoal().y) //User clicked on the tile the ball is on, they want to kick it
        {
            velocity = this.getWalkOnVelocity(roomUnit, room);
            direction = this.getWalkOnDirection(roomUnit, room);
            this.onKick(room, roomUnit, velocity, direction);
        }
        else //User is walking past the ball, they want to drag it with them
        {
            velocity = this.getDragVelocity(roomUnit, room);
            direction = this.getDragDirection(roomUnit, room);
            this.onDrag(room, roomUnit, velocity, direction);
        }
        
        if(velocity > 0)
        {
            if(currentThread != null)
                currentThread.dead = true;
            
            currentThread = new KickBallAction(this, room, roomUnit, direction, velocity);
            Emulator.getThreading().run(currentThread, 0);
        }
    }

    /**
     * Gets the velocity of the item when a Habbo kicks the item
     * 
     * @param roomUnit The Habbo that initiated the event
     * @param room The room this event happened in
     * @return The item's new velocity
     */
    public abstract int getWalkOnVelocity(RoomUnit roomUnit, Room room);
    
    /**
     * Gets the direction the item should start moving in when a Habbo kicks the item
     * 
     * @param roomUnit The Habbo that initiated the event
     * @param room The room this event happened in
     * @return Direction that the item should start moving in
     */
    public abstract RoomUserRotation getWalkOnDirection(RoomUnit roomUnit, Room room);
    
    /**
     * Gets the velocity of the item when a Habbo walks off the item
     * 
     * @param roomUnit The Habbo that initiated the event
     * @param room The room this event happened in
     * @return The item's new velocity
     */
    public abstract int getWalkOffVelocity(RoomUnit roomUnit, Room room);
    
    /**
     * Gets the direction the item should start moving in when a Habbo walks off the item
     * 
     * @param roomUnit The Habbo that initiated the event
     * @param room The room this event happened in
     * @return Direction that the item should start moving in
     */
    public abstract RoomUserRotation getWalkOffDirection(RoomUnit roomUnit, Room room);
    
    /**
     * Gets the velocity of the item when a Habbo drags the item
     * 
     * @param roomUnit The Habbo that initiated the event
     * @param room The room this event happened in
     * @return The item's new velocity
     */
    public abstract int getDragVelocity(RoomUnit roomUnit, Room room);
    
    /**
     * Gets the direction the item should start moving in when a Habbo drags the item
     * 
     * @param roomUnit The Habbo that initiated the event
     * @param room The room this event happened in
     * @return Direction that the item should start moving in
     */
    public abstract RoomUserRotation getDragDirection(RoomUnit roomUnit, Room room);
    
    /**
     * Gets the velocity of the item when a Habbo tackles the item
     * 
     * @param roomUnit The Habbo that initiated the event
     * @param room The room this event happened in
     * @return The item's new velocity
     */
    public abstract int getTackleVelocity(RoomUnit roomUnit, Room room);
    
    /**
     * Gets the direction the item should start moving in when a Habbo tackles the item
     * 
     * @param roomUnit The Habbo that initiated the event
     * @param room The room this event happened in
     * @return Direction that the item should start moving in
     */
    public abstract RoomUserRotation getTackleDirection(RoomUnit roomUnit, Room room);
    
    /**
     * Gets the delay till the next move of the item in milliseconds.
     * 
     * @param currentStep The current step of the move sequence
     * @param totalSteps The total number of steps to complete the move sequence
     * @return Time in milliseconds till the next move of the item
     */
    public abstract int getNextRollDelay(int currentStep, int totalSteps); //The length in milliseconds when the ball should next roll
    
    /**
     * Gets the new direction of the item when it cannot move any further.
     * 
     * @param room The room this event happened in
     * @param currentDirection The current direction of the item
     * @return New direction of the item
     */
    public abstract RoomUserRotation getBounceDirection(Room room, RoomUserRotation currentDirection); //Returns the new direction to move the ball when the ball cannot move
    
    /**
     * Checks if the next move is allowed.
     * 
     * @param room The room this event happened in
     * @param from The current coordinate of the item
     * @param to The coordinate the item wishes to move to
     * @return Is this move allowed? 
     */
    public abstract boolean validMove(Room room, RoomTile from, RoomTile to); //Checks if the next move is valid
    
    /**
     * Triggered when a Habbo drags a pushable item.
     * 
     * @param room The room that this event happened in
     * @param roomUnit The Habbo that initiated the event
     * @param velocity The new velocity of the item
     * @param direction The direction the item is moving in
     */
    public abstract void onDrag(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction);
    
    /**
     * Triggered when a Habbo kicks a pushable item.
     * 
     * @param room The room that this event happened in
     * @param roomUnit The Habbo that initiated the event
     * @param velocity The new velocity of the item
     * @param direction The direction the item is moving in
     */
    public abstract void onKick(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction);
    
    /**
     * Triggered when a Habbo tackles the ball (double clicks).
     * 
     * @param room The room that this event happened in
     * @param roomUnit The Habbo that initiated the event
     * @param velocity The new velocity of the item
     * @param direction The direction the item is moving in
     */
    public abstract void onTackle(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction);
    
    /**
     * Triggered when an item is moved due to a kick, drag or tackle.
     * State changes should be executed here
     * 
     * @param room The room that the event happened in
     * @param from The old coordinate of the item
     * @param to The new coordinate of the item
     * @param direction The direction the item is moving in
     * @param kicker The Habbo which initiated the move of the item
     * @param nextRoll The delay till the next move of the item
     * @param currentStep The current step of the move sequence
     * @param totalSteps The total number of steps to complete the move sequence
     */
    public abstract void onMove(Room room, RoomTile from, RoomTile to, RoomUserRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps);
    
    /**
     * Triggered when an item cannot move any further and has changed it's direction.
     * 
     * @param room The room that the event happened in
     * @param oldDirection The old direction of the item
     * @param newDirection The new direction of the item
     * @param kicker The Habbo which initiated the move of the item
     */
    public abstract void onBounce(Room room, RoomUserRotation oldDirection, RoomUserRotation newDirection, RoomUnit kicker);
    
    /**
     * Triggered when an item stops moving
     * 
     * @param room The room that the event happened in
     * @param kicker The Habbo which initiated the move of the item
     * @param currentStep The current step of the move sequence
     * @param totalSteps The total number of steps to complete the move sequence
     */
    public abstract void onStop(Room room, RoomUnit kicker, int currentStep, int totalSteps);
    
    /**
     * Checks if the item is still allowed to move. If not, the ball will stop.
     * 
     * @param room The room that the event happened in
     * @param from The old coordinate of the item
     * @param to The new coordinate of the item
     * @param direction The direction the item is moving in
     * @param kicker The Habbo which initiated the move of the item
     * @param nextRoll The delay till the next move of the item
     * @param currentStep The current step of the move sequence
     * @param totalSteps The total number of steps to complete the move sequence
     * @return Can the item still move?
     */
    public abstract boolean canStillMove(Room room, RoomTile from, RoomTile to, RoomUserRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps);
}
