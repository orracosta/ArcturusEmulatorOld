package com.eu.habbo.habbohotel.items.interactions.games.battlebanzai;

import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBattleBanzaiTile extends HabboItem
{
    public InteractionBattleBanzaiTile(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionBattleBanzaiTile(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects)  throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        if(this.getExtradata().isEmpty())
            this.setExtradata("0");

        int state = Integer.valueOf(this.getExtradata());

        if(state % 3 == 2)
            return;

        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo == null)
            return;

        if(this.isLocked())
            return;

        if(habbo.getHabboInfo().getCurrentGame() != null && habbo.getHabboInfo().getCurrentGame().equals(BattleBanzaiGame.class))
        {
            BattleBanzaiGame game = ((BattleBanzaiGame)room.getGame(BattleBanzaiGame.class));

            if(game == null)
                return;

            if(!game.isRunning)
                return;


            int check = state - (habbo.getHabboInfo().getGamePlayer().getTeamColor().type * 3);
            if(check == 3 || check == 4)
            {
                state++;

                if(state % 3 == 2)
                {
                    habbo.getHabboInfo().getGamePlayer().addScore(BattleBanzaiGame.POINTS_LOCK_TILE);
                    game.tileLocked(habbo.getHabboInfo().getGamePlayer().getTeamColor(), this, habbo);
                }
                else
                {
                    habbo.getHabboInfo().getGamePlayer().addScore(BattleBanzaiGame.POINTS_FILL_TILE);
                }
            }
            else
            {
                state = (habbo.getHabboInfo().getGamePlayer().getTeamColor().type * 3) + 3;

                habbo.getHabboInfo().getGamePlayer().addScore(BattleBanzaiGame.POINTS_HIJACK_TILE);
            }

            game.refreshCounters(habbo.getHabboInfo().getGamePlayer().getTeamColor());
            this.setExtradata(state + "");
            room.updateItem(this);
        }

    }

    public boolean isLocked()
    {
        if(this.getExtradata().isEmpty())
            return false;

        return Integer.valueOf(this.getExtradata()) % 3 == 2;
    }
}
