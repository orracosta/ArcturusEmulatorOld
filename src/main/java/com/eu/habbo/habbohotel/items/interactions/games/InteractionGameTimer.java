package com.eu.habbo.habbohotel.items.interactions.games;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.wired.WiredGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionGameTimer extends HabboItem
{
    private int baseTime = 0;

    protected InteractionGameTimer(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);

        String[] data = set.getString("extra_data").split("\t");

        if (data.length >= 2)
        {
            this.baseTime = Integer.valueOf(data[1]);
        }

        if (data.length >= 1)
        {
            this.setExtradata(data[0]);
        }
    }

    protected InteractionGameTimer(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onPickUp(Room room)
    {
        this.setExtradata("0");
    }

    @Override
    public void onPlace(Room room)
    {
        this.baseTime = 30;
        this.setExtradata("30");
        room.updateItem(this);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if (client != null)
        {
            if (!(room.hasRights(client.getHabbo()) || client.getHabbo().hasPermission("acc_anyroomowner")))
                return;
        }

        if(this.getExtradata().isEmpty())
        {
            this.setExtradata("0");
        }

        Game game = room.getGame(WiredGame.class);

        if (game == null)
        {
            game = (this.getGameType().cast(room.getGame(this.getGameType())));
        }

        if ((objects.length >= 2 && objects[1] instanceof WiredEffectType))
        {
            if (game.isRunning)
                return;
        }

        if(objects.length >= 1 && objects[0] instanceof Integer && client != null)
        {
            int state = (Integer)objects[0];

            switch (state)
            {
                case 1:
                {
                    startGame(room);
                    break;
                }

                case 2:
                {
                    increaseTimer(room);
                }
                break;

                case 3:
                {
                    stopGame(room);
                }
                break;
            }
        }
        else
        {

            if (game != null && !game.isRunning)
            {
                this.startGame(room);
            }
        }

        super.onClick(client, room, objects);
    }

    private void startGame(Room room)
    {
        this.needsUpdate(true);
        try
        {
            this.setExtradata(this.baseTime + "");

            room.updateItem(this);

            Game game = (this.getGameType().cast(room.getGame(this.getGameType())));

            if (game == null)
            {
                game = this.getGameType().getDeclaredConstructor(Room.class).newInstance(room);
                room.addGame(game);
            }

            if (!game.isRunning)
            {
                game.initialise();
            }
            else
            {
                game.stop();
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    private void stopGame(Room room)
    {
        this.needsUpdate(true);
        Game game = (this.getGameType().cast(room.getGame(this.getGameType())));

        if(game != null && game.isRunning)
        {
            game.stop();
        }

        room.updateItem(this);
    }

    private void increaseTimer(Room room)
    {
        Game game = (this.getGameType().cast(room.getGame(this.getGameType())));

        if(game != null && game.isRunning)
        {
            return;
        }

        this.needsUpdate(true);
        switch(this.baseTime)
        {
            case 0:     this.baseTime = 30; break;
            case 30:    this.baseTime = 60; break;
            case 60:    this.baseTime = 120; break;
            case 120:   this.baseTime = 180; break;
            case 180:   this.baseTime = 300; break;
            case 300:   this.baseTime = 600; break;
            //case 600:   this.baseTime = 0; break;

            default:
                this.baseTime = 30;
        }

        this.setExtradata(this.baseTime + "");

        room.updateItem(this);
    }

    @Override
    public String getDatabaseExtraData()
    {
        return this.getExtradata() + "\t" + this.baseTime;
    }

    public abstract Class<? extends Game> getGameType();
}
