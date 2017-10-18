package com.eu.habbo.habbohotel.items.interactions.games.football;

import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.football.FootballGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionPushable;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTeamItem;
import com.eu.habbo.habbohotel.items.interactions.games.football.goals.InteractionFootballGoal;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.ItemStateComposer;
import com.eu.habbo.util.pathfinding.Rotation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFootball extends InteractionPushable
{
    public InteractionFootball(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionFootball(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }
    
    // Velocity
    
    @Override
    public int getWalkOnVelocity(RoomUnit roomUnit, Room room)
    {
        if(roomUnit.getPath().isEmpty() && roomUnit.tilesWalked() == 2)
            return 0;
        
        return 6;
    }
    
    @Override
    public int getWalkOffVelocity(RoomUnit roomUnit, Room room)
    {
        return 6;
    }
    
    @Override
    public int getDragVelocity(RoomUnit roomUnit, Room room)
    {
        if(roomUnit.getPath().isEmpty() && roomUnit.tilesWalked() == 2)
            return 0;
        
        return 1;
    }
    
    @Override
    public int getTackleVelocity(RoomUnit roomUnit, Room room)
    {        
        return 2;
    }
    
    // Direction
        
    @Override
    public RoomUserRotation getWalkOnDirection(RoomUnit roomUnit, Room room)
    {
        return roomUnit.getBodyRotation();
    }
    
    @Override
    public RoomUserRotation getWalkOffDirection(RoomUnit roomUnit, Room room)
    {
        RoomTile nextWalkTile = roomUnit.getPath().peek();

        if (nextWalkTile == null)
        {
            nextWalkTile = roomUnit.getGoal();
        }

        if (nextWalkTile != null)
        {
            return RoomUserRotation.values()[(RoomUserRotation.values().length + Rotation.Calculate(roomUnit.getX(), roomUnit.getY(), nextWalkTile.x, nextWalkTile.y) + 4) % 8];
        }
        else
        {
            return RoomUserRotation.values()[(roomUnit.getBodyRotation().getValue() + 4) % RoomUserRotation.values().length];
        }
    }
    
    public RoomUserRotation getDragDirection(RoomUnit roomUnit, Room room)
    {
        return roomUnit.getBodyRotation();
    }
    
    public RoomUserRotation getTackleDirection(RoomUnit roomUnit, Room room)
    {
        return roomUnit.getBodyRotation();
    }
    
    // Methods
    
    @Override
    public int getNextRollDelay(int currentStep, int totalSteps)
    {
        int t = 2500;
        return (totalSteps == 1) ? 500 : 100*((t=t/t-1)*t*t*t*t + 1) + (currentStep * 100);
    }
    
    @Override
    public RoomUserRotation getBounceDirection(Room room, RoomUserRotation currentDirection)
    {
        switch(currentDirection)
        {
            default:
            case NORTH:
                return RoomUserRotation.SOUTH;
            
            case NORTH_EAST:
                if(this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_WEST.getValue())))
                    return RoomUserRotation.NORTH_WEST;
                else if(this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_EAST.getValue())))
                    return RoomUserRotation.SOUTH_EAST;
                else
                    return RoomUserRotation.SOUTH_WEST;
                
            case EAST:
                return RoomUserRotation.WEST;
                
            case SOUTH_EAST:
                if(this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_WEST.getValue())))
                    return RoomUserRotation.SOUTH_WEST;
                else if(this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_EAST.getValue())))
                    return RoomUserRotation.NORTH_EAST;
                else
                    return RoomUserRotation.NORTH_WEST;
                
            case SOUTH:
                return RoomUserRotation.NORTH;
                
            case SOUTH_WEST:
                if(this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_EAST.getValue())))
                    return RoomUserRotation.SOUTH_EAST;
                else if(this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_WEST.getValue())))
                    return RoomUserRotation.NORTH_WEST;
                else
                    return RoomUserRotation.NORTH_EAST;
                
            case WEST:
                return RoomUserRotation.EAST;
                
            case NORTH_WEST:
                if(this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.NORTH_EAST.getValue())))
                    return RoomUserRotation.NORTH_EAST;
                else if(this.validMove(room, room.getLayout().getTile(this.getX(), this.getY()), room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), RoomUserRotation.SOUTH_WEST.getValue())))
                    return RoomUserRotation.SOUTH_WEST;
                else
                    return RoomUserRotation.SOUTH_EAST;
        }
    }
    
    // Checks
    
    @Override
    public boolean validMove(Room room, RoomTile from, RoomTile to)
    {
        if (to == null || to.state == RoomTileState.BLOCKED)
            return false;

        HabboItem topItem = room.getTopItemAt(to.x, to.y, this);
        return !(!room.getLayout().tileWalkable(to.x, to.y) || (topItem != null && (!topItem.getBaseItem().allowStack() || topItem.getBaseItem().allowSit() || topItem.getBaseItem().allowLay())));
    }
    
    //Events
    
    @Override
    public void onDrag(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction)
    {
        
    }
    
    @Override
    public void onKick(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction)
    {
        
    }
    
    @Override
    public void onTackle(Room room, RoomUnit roomUnit, int velocity, RoomUserRotation direction)
    {
        
    }
    
    @Override
    public void onMove(Room room, RoomTile from, RoomTile to, RoomUserRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps)
    {
        FootballGame game = (FootballGame)room.getGame(FootballGame.class);
        if (game == null)
        {
            try
            {
                game = FootballGame.class.getDeclaredConstructor(Room.class).newInstance(room);
                room.addGame(game);
            }
            catch(Exception e)
            {
                return;
            }
        }

        HabboItem currentTopItem = room.getTopItemAt(from.x, from.y, this);
        HabboItem topItem = room.getTopItemAt(to.x, to.y, this);

        if(game != null && topItem != null && (currentTopItem == null || currentTopItem.getId() != topItem.getId()) && topItem instanceof InteractionFootballGoal)
        {
            GameTeamColors color = ((InteractionGameTeamItem) topItem).teamColor;
            game.onScore(kicker, color);
        }

        this.setExtradata(nextRoll <= 200 ? "8" : (nextRoll <= 250 ? "7" : (nextRoll <= 300 ? "6" : (nextRoll <= 350 ? "5" : (nextRoll <= 400 ? "4" : (nextRoll <= 450 ? "3" : (nextRoll <= 500 ? "2" : "1")))))));
        room.sendComposer(new ItemStateComposer(this).compose());
    }
    
    @Override
    public void onBounce(Room room, RoomUserRotation oldDirection, RoomUserRotation newDirection, RoomUnit kicker)
    {
        
    }
    
    @Override
    public void onStop(Room room, RoomUnit kicker, int currentStep, int totalSteps)
    {
        this.setExtradata("0");
        room.sendComposer(new ItemStateComposer(this).compose());
    }
    
    @Override
    public boolean canStillMove(Room room, RoomTile from, RoomTile to, RoomUserRotation direction, RoomUnit kicker, int nextRoll, int currentStep, int totalSteps)
    {
        if (from == null || to == null)
            return false;

        HabboItem topItem = room.getTopItemAt(from.x, from.y, this);
        return !(room.hasHabbosAt(to.x, to.y) || (topItem != null && topItem.getBaseItem().getName().startsWith("fball_goal_") && currentStep != 1));
    }
    
    @Override
    public void onPickUp(Room room)
    {
        this.setExtradata("0");
    }
}
