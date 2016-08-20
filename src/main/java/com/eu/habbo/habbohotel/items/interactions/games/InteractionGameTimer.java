package com.eu.habbo.habbohotel.items.interactions.games;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;

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

        if (data.length == 3)
        {
            boolean gameRunning = data[2].equals("1");

            if (gameRunning && this.getRoomId() > 0)
            {
                this.startGame(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()));
            }
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
        if (client != null)
        {
            if (!(room.hasRights(client.getHabbo()) || client.getHabbo().hasPermission("acc_anyroomowner")))
                return;
        }

        if(this.getExtradata().isEmpty())
        {
            this.setExtradata("0");
        }

        if(objects.length >= 1 && objects[0] instanceof Integer && client != null && !(objects.length >= 2 && objects[1] instanceof WiredEffectType))
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
                    stopGame(room);
                    increaseTimer();
                }
                break;

                case 3:
                {
                    stopGame(room);
                }
                break;
            }

            room.updateItem(this);
        }
        else
        {
            Game game = (this.getGameType().cast(room.getGame(this.getGameType())));

            if (game != null && game.isRunning)
            {
                this.stopGame(room);
            }
            else
            {
                this.startGame(room);
            }
        }

        super.onClick(client, room, objects);
    }

    private void startGame(Room room)
    {
        try
        {
            this.setExtradata(this.baseTime + "");

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
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    private void stopGame(Room room)
    {
        Game game = (this.getGameType().cast(room.getGame(this.getGameType())));

        if(game != null && game.isRunning)
        {
            game.stop();
        }
    }

    private void increaseTimer()
    {
        switch(this.baseTime)
        {
            case 0:     this.baseTime = 30; break;
            case 30:    this.baseTime = 60; break;
            case 60:    this.baseTime = 120; break;
            case 120:   this.baseTime = 180; break;
            case 180:   this.baseTime = 300; break;
            case 300:   this.baseTime = 600; break;
            case 600:   this.baseTime = 0; break;

            default:
                this.baseTime = 30;
        }

        this.setExtradata(this.baseTime + "");
    }

    @Override
    public String getDatabaseExtraData()
    {
        String running = "0";

        if (this.getRoomId() != 0)
        {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
            Game game = (this.getGameType().cast(room.getGame(this.getGameType())));

            if (game != null && game.isRunning)
            {
                running = "1";
            }
        }

        return this.getExtradata() + "\t" + this.baseTime + "\t" + running;
    }

    protected abstract Class<? extends Game> getGameType();
}
