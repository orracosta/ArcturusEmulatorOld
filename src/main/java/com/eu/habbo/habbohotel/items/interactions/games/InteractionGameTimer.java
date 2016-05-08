package com.eu.habbo.habbohotel.items.interactions.games;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 24-1-2015 10:00.
 */
public abstract class InteractionGameTimer extends HabboItem
{
    protected InteractionGameTimer(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    protected InteractionGameTimer(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onPickUp()
    {
        this.setExtradata("0");
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt32((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(!(room.hasRights(client.getHabbo()) || client.getHabbo().hasPermission("acc_anyroomowner")))
            return;

        if(this.getExtradata().isEmpty())
        {
            this.setExtradata("0");
        }

        if(objects.length >= 1)
        {
            if(objects[0] instanceof Integer)
            {
                int state = (Integer)objects[0];
                int newState = Integer.valueOf(this.getExtradata());

                switch (state)
                {
                    case 1:
                    {

                        Game game = (this.getGameType().cast(room.getGame(this.getGameType())));

                        if(game == null)
                        {
                            game = this.getGameType().getDeclaredConstructor(Room.class).newInstance(room);
                            room.addGame(game);
                        }
                        game.initialise();

                        break;
                    }

                    case 2:
                    {
                        switch(newState)
                        {
                            case 0:     newState = 30; break;
                            case 30:    newState = 60; break;
                            case 60:    newState = 120; break;
                            case 120:   newState = 180; break;
                            case 180:   newState = 300; break;
                            case 300:   newState = 600; break;
                            case 600:   newState = 0; break;

                            default:
                                newState = 30;
                        }
                    }
                    break;

                    case 3:
                    {
                        Game game = (this.getGameType().cast(room.getGame(this.getGameType())));

                        if(game != null)
                        {
                            game.stop();
                        }
                    }
                }

                if(newState < 0)
                    newState = 0;

                this.setExtradata(newState + "");
                room.updateItem(this);
            }
        }

        super.onClick(client, room, objects);
    }

    protected abstract Class<? extends Game> getGameType();
}
