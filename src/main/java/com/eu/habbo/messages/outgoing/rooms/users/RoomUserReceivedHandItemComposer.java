package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 2-8-2015 16:04.
 */
public class RoomUserReceivedHandItemComposer extends MessageComposer
{
    private RoomUnit from;
    private int handItem;

    public RoomUserReceivedHandItemComposer(RoomUnit from, int handItem)
    {
        this.from = from;
        this.handItem = handItem;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserReceivedHandItemComposer);
        this.response.appendInt32(this.from.getId());
        this.response.appendInt32(this.handItem);
        return this.response;
    }
}
