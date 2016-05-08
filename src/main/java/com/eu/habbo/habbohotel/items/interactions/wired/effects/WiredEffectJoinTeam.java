package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 20:18.
 */
public class WiredEffectJoinTeam extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.JOIN_TEAM;

    private GameTeamColors teamColor = GameTeamColors.RED;

    public WiredEffectJoinTeam(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectJoinTeam(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo != null)
        {
            if(habbo.getHabboInfo().getGamePlayer() == null)
            {
                Game game = room.getGame(Game.class);

                if(game != null)
                {
                    return game.addHabbo(habbo, this.teamColor);
                }
            }
        }

        return false;
    }

    @Override
    public String getWiredData()
    {
        return this.teamColor.type + "";
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.teamColor = GameTeamColors.values()[Integer.valueOf(set.getString("wired_data"))];
    }

    @Override
    public void onPickUp()
    {
        this.teamColor = GameTeamColors.RED;
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
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
        message.appendInt32(1);
        message.appendInt32(this.teamColor.type + 1);
        message.appendInt32(0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.teamColor = GameTeamColors.values()[packet.readInt() - 1];

        return true;
    }
}
