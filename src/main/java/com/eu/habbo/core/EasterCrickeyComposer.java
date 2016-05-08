package com.eu.habbo.core;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 24-10-2015 20:45.
 */
public class EasterCrickeyComposer extends MessageComposer
{
    private final Habbo habbo;

    public EasterCrickeyComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUsersComposer);
        this.response.appendInt32(1);
        this.response.appendInt32(this.habbo.getHabboInfo().getId());
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        this.response.appendString("");
        this.response.appendString("2 1 88E70D 2 2 -1 0 3 -1 0");
        this.response.appendInt32(habbo.getRoomUnit().getId());
        this.response.appendInt32(habbo.getRoomUnit().getX());
        this.response.appendInt32(habbo.getRoomUnit().getY());
        this.response.appendString(habbo.getRoomUnit().getZ() + "");
        this.response.appendInt32(0);
        this.response.appendInt32(2);
        this.response.appendInt32(2);
        this.response.appendInt32(this.habbo.getHabboInfo().getId());
        this.response.appendString("Crickey!"); //TODO Owner name
        this.response.appendInt32(1);
        this.response.appendBoolean(false);
        this.response.appendBoolean(true);
        this.response.appendBoolean(true); //Can toggle breeding permissions.
        this.response.appendBoolean(true);
        this.response.appendBoolean(true); //Can treat?
        this.response.appendBoolean(true); //Can breed
        this.response.appendInt32(0);
        this.response.appendString("");
        return this.response;
    }
}
