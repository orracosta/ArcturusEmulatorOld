package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 3-9-2014 09:19.
 */
public class RoomOwnerComposer extends MessageComposer {
    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomOwnerComposer);
        return this.response;
    }
}
