package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHighscoreClearType;
import com.eu.habbo.habbohotel.wired.WiredHighscoreData;
import com.eu.habbo.habbohotel.wired.WiredHighscoreScoreType;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionWiredHighscore extends HabboItem
{
    public WiredHighscoreScoreType scoreType;
    public WiredHighscoreClearType clearType;

    private THashSet<WiredHighscoreData> data;
    private int lastUpdate;

    public InteractionWiredHighscore(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);

        this.scoreType = WiredHighscoreScoreType.CLASSIC;
        this.clearType = WiredHighscoreClearType.ALLTIME;

        try
        {
            String name = this.getBaseItem().getName().split("_")[1].toUpperCase().split("\\*")[0];
            int ctype = Integer.valueOf(this.getBaseItem().getName().split("\\*")[1]) - 1;
            this.scoreType = WiredHighscoreScoreType.valueOf(name);
            this.clearType = WiredHighscoreClearType.values()[ctype];
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        if(this.getRoomId() > 0)
        {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

            if(room != null)
            {
                this.reloadData(room);
            }
        }
    }

    public InteractionWiredHighscore(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);

        this.scoreType = WiredHighscoreScoreType.CLASSIC;
        this.clearType = WiredHighscoreClearType.ALLTIME;

        try
        {
            String name = this.getBaseItem().getName().split("_")[1].toUpperCase().split("\\*")[0];
            int ctype = Integer.valueOf(this.getBaseItem().getName().split("\\*")[1]) - 1;
            this.scoreType = WiredHighscoreScoreType.valueOf(name);
            this.clearType = WiredHighscoreClearType.values()[ctype];
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
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
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(this.getExtradata() == null || this.getExtradata().isEmpty() || this.getExtradata().length() == 0)
        {
            this.setExtradata("0");
        }

        try
        {
            int state = Integer.valueOf(this.getExtradata());
            this.setExtradata(Math.abs(state - 1) + "");
            this.reloadData(room);
            room.updateItem(this);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    /*

    FUCK OFF
     */
    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt(6);
        serverMessage.appendString(this.getExtradata());
        serverMessage.appendInt(this.scoreType.type); //score type
        serverMessage.appendInt(this.clearType.type); //clear type

        if(this.getRoomId() == 0)
        {
            serverMessage.appendInt(0);
        }
        else
        {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

            if(room == null)
            {
                serverMessage.appendInt(0);
            }
            else
            {
                if(Emulator.getIntUnixTimestamp() - this.lastUpdate > 60 * 60)
                {
                    this.reloadData(room);
                }

                if(this.data != null)
                {
                    serverMessage.appendInt(this.data.size()); //count

                    for (WiredHighscoreData dataSet : this.data)
                    {
                        if (this.scoreType == WiredHighscoreScoreType.PERTEAM)
                        {
                            serverMessage.appendInt(dataSet.teamScore); //Team score
                        } else if (dataSet.usernames.length == 1)
                        {
                            serverMessage.appendInt(dataSet.score);
                        } else
                        {
                            serverMessage.appendInt(dataSet.totalScore);
                        }

                        serverMessage.appendInt(dataSet.usernames.length); //Users count

                        for (String codeDragon : dataSet.usernames)
                        {
                            serverMessage.appendString(codeDragon);
                        }
                    }
                }
                else
                {
                    serverMessage.appendInt(0);
                }
            }
        }

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onPlace(Room room)
    {
        this.reloadData(room);
    }

    @Override
    public void onPickUp(Room room)
    {
        if (this.data != null)
        {
            this.data.clear();
        }
        this.lastUpdate = 0;
    }

    private void reloadData(Room room)
    {
        this.data = room.getWiredHighscoreData(this.scoreType, this.clearType);
        this.lastUpdate = Emulator.getIntUnixTimestamp();
    }
}
