package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildFavoriteRoomUserUpdateComposer extends MessageComposer
{
    private RoomUnit roomUnit;
    private Guild guild;

    public GuildFavoriteRoomUserUpdateComposer(RoomUnit roomUnit, Guild guild)
    {
        this.roomUnit = roomUnit;
        this.guild = guild;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildFavoriteRoomUserUpdateComposer);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.guild.getId());
        this.response.appendInt(this.guild.getState().state);
        this.response.appendString(this.guild.getName());
        return this.response;
    }
}
