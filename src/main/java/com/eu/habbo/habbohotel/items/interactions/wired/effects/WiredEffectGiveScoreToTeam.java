package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.map.hash.TIntIntHashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveScoreToTeam extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.GIVE_SCORE_TEAM;

    private int points;
    private int count;
    private GameTeamColors teamColor = GameTeamColors.RED;

    private TIntIntHashMap startTimes = new TIntIntHashMap();

    public WiredEffectGiveScoreToTeam(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public WiredEffectGiveScoreToTeam(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo != null)
        {
            Class<? extends Game> game = habbo.getHabboInfo().getCurrentGame();

            if(game != null)
            {
                Game g = room.getGame(game);

                if(g != null)
                {
                    int c = this.startTimes.get(g.getStartTime());

                    if(c < this.count)
                    {
                        GameTeam team = g.getTeam(this.teamColor);

                        if(team != null)
                        {
                            team.addTeamScore(this.points);

                            this.startTimes.put(g.getStartTime(), c++);

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String getWiredData()
    {
        return this.points + ";" + this.count + ";" + this.teamColor.type + ";" + this.getDelay();
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split(";");

        if(data.length == 4)
        {
            this.points = Integer.valueOf(data[0]);
            this.count = Integer.valueOf(data[1]);
            this.teamColor = GameTeamColors.values()[Integer.valueOf(data[2])];
            this.setDelay(Integer.valueOf(data[3]));
        }
    }

    @Override
    public void onPickUp()
    {
        this.startTimes.clear();
        this.points = 0;
        this.count = 0;
        this.teamColor = GameTeamColors.RED;
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(3);
            message.appendInt(this.points);
            message.appendInt(this.count);
            message.appendInt(this.teamColor.type + 1);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient)
    {
        packet.readInt();

        this.points = packet.readInt();
        this.count = packet.readInt();
        this.teamColor = GameTeamColors.values()[packet.readInt() - 1];
        packet.readString();
        packet.readInt();
        this.setDelay(packet.readInt());
        return true;
    }
}
