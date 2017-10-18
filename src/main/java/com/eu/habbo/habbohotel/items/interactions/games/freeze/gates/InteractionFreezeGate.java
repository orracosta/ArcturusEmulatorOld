package com.eu.habbo.habbohotel.items.interactions.games.freeze.gates;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGamePlayer;
import com.eu.habbo.habbohotel.games.freeze.FreezeGameTeam;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFreezeGate extends InteractionGameGate
{
    public InteractionFreezeGate(ResultSet set, Item baseItem, GameTeamColors teamColor) throws SQLException
    {
        super(set, baseItem, teamColor);
    }

    public InteractionFreezeGate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells, GameTeamColors teamColor)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells, teamColor);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return room.getGame(FreezeGame.class) == null || !((FreezeGame)room.getGame(FreezeGame.class)).isRunning;
    }

    @Override
    public boolean isWalkable()
    {
        if (this.getRoomId() == 0)
            return false;

        return (this.getExtradata().isEmpty() ||
                Integer.valueOf(this.getExtradata()) < 5);
                //((Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getGame(FreezeGame.class))) == null ||
                //!((FreezeGame)(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getGame(FreezeGame.class))).isRunning;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects)  throws Exception
    {
        FreezeGame game = (FreezeGame) room.getGame(FreezeGame.class);

        if(game == null)
        {
            game = FreezeGame.class.getDeclaredConstructor(Room.class).newInstance(room);
            room.addGame(game);
        }

        GameTeam team = game.getTeamForHabbo(room.getHabbo(roomUnit));

        if(team != null)
        {
            game.removeHabbo(room.getHabbo(roomUnit));
        }
        else
        {
            if(this.getExtradata().isEmpty())
            {
                this.setExtradata("0");
            }

            int value = Integer.valueOf(this.getExtradata()) + 1;

            this.setExtradata(value + "");
            room.updateItem(this);
            game.addHabbo(room.getHabbo(roomUnit), this.teamColor);
        }

        super.onWalkOn(roomUnit, room, objects);
    }
}
