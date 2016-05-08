package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 19-6-2015 13:16.
 */
public class NewNavigatorMetaDataComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewNavigatorMetaDataComposer);
        this.response.appendInt32(4);

        this.response.appendString("official_view");
        this.response.appendInt32(0);
        this.response.appendString("hotel_view");
        this.response.appendInt32(0);
        this.response.appendString("roomads_view");
        this.response.appendInt32(0);
        this.response.appendString("myworld_view");
        this.response.appendInt32(0);

        return this.response;
    }
}
