package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 6-9-2014 13:55.
 */
public class RoomOpenComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomOpenComposer);
        return this.response;
    }
}
