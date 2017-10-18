package com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGamePlayer;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGameTeam;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBattleBanzaiGate extends InteractionGameGate
{
    public InteractionBattleBanzaiGate(ResultSet set, Item baseItem, GameTeamColors teamColor) throws SQLException
    {
        super(set, baseItem, teamColor);
    }

    public InteractionBattleBanzaiGate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells, GameTeamColors teamColor)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells, teamColor);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return room.getGame(BattleBanzaiGame.class) == null || !((BattleBanzaiGame)room.getGame(BattleBanzaiGame.class)).isRunning;
    }

    @Override
    public boolean isWalkable()
    {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
        if(room == null)
            return false;

        return (this.getExtradata() == null || this.getExtradata().isEmpty() || Integer.valueOf(this.getExtradata()) < 5) && ((room.getGame(BattleBanzaiGame.class))) == null || !((BattleBanzaiGame)(room.getGame(BattleBanzaiGame.class))).isRunning;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    //TODO: Move to upper class
    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects)  throws Exception
    {
        BattleBanzaiGame game = (BattleBanzaiGame) room.getGame(BattleBanzaiGame.class);

        if(game == null)
        {
            game = BattleBanzaiGame.class.getDeclaredConstructor(Room.class).newInstance(room);
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
