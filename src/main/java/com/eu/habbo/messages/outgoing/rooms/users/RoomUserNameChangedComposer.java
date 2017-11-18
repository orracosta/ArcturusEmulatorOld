package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomUserNameChangedComposer extends MessageComposer
{
    private final Habbo habbo;
    private final boolean includePrefix;

    public RoomUserNameChangedComposer(Habbo habbo)
    {
        this.habbo = habbo;
        this.includePrefix = false;
    }

    public RoomUserNameChangedComposer(Habbo habbo, boolean includePrefix)
    {
        this.habbo = habbo;
        this.includePrefix = includePrefix;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserNameChangedComposer);
        this.response.appendInt(this.habbo.getHabboInfo().getId());
        this.response.appendInt(this.habbo.getRoomUnit().getId());
        this.response.appendString((this.includePrefix ? Room.PREFIX_FORMAT.replace("%color%", this.habbo.getHabboInfo().getRank().getPrefixColor()).replace("%prefix%", this.habbo.getHabboInfo().getRank().getPrefix()) : "") + this.habbo.getHabboInfo().getUsername());
        return this.response;
    }
}