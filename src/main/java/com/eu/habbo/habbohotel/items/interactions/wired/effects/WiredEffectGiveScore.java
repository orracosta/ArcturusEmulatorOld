package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Created on 13-12-2014 20:14.
 */
public class WiredEffectGiveScore extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.GIVE_SCORE;

    private int score;
    private int count;

    private TObjectIntMap<Map.Entry<Integer, Integer>> data = new TObjectIntHashMap<Map.Entry<Integer, Integer>>();

    public WiredEffectGiveScore(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectGiveScore(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo != null)
        {
            Game game = room.getGame(habbo.getHabboInfo().getCurrentGame());

            if(game == null)
                return false;

            TObjectIntIterator<Map.Entry<Integer, Integer>> iterator = this.data.iterator();

            for(int i = this.data.size(); i-- > 0; )
            {
                iterator.advance();

                Map.Entry<Integer, Integer> map = iterator.key();

                if(map.getValue() == habbo.getHabboInfo().getId())
                {
                    if(map.getKey() == game.getStartTime())
                    {
                        if(iterator.value() < this.count)
                        {
                            iterator.setValue(iterator.value() + 1);

                            habbo.getHabboInfo().getGamePlayer().addScore(this.score);

                            return true;
                        }
                    }
                    else
                    {
                        iterator.remove();
                    }
                }
            }

            this.data.put(new AbstractMap.SimpleEntry<Integer, Integer>(game.getStartTime(), habbo.getHabboInfo().getId()), 1);

            habbo.getHabboInfo().getGamePlayer().addScore(this.score);

            return true;
        }

        return false;
    }

    @Override
    public String getWiredData()
    {
        return this.score + ";" + this.count;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split(";");

        if(data.length == 2)
        {
            this.score = Integer.valueOf(data[0]);
            this.count = Integer.valueOf(data[1]);
        }
    }

    @Override
    public void onPickUp()
    {
        this.score = 0;
        this.count = 0;
    }

    @Override
    public WiredEffectType getType()
    {
        return WiredEffectGiveScore.type;
    }

    @Override
    public void serializeWiredData(ServerMessage message)
    {
        message.appendBoolean(false);
        message.appendInt32(5);
        message.appendInt32(0);
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString("");
        message.appendInt32(2);
        message.appendInt32(this.score);
        message.appendInt32(this.count);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.score = packet.readInt();
        this.count = packet.readInt();

        return true;
    }
}
