package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 3-3-2015 13:17.
 */
public class ReportRoomFormComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ReportRoomFormComposer);
        this.response.appendInt32(0); //Current standing help request(s) amount:
        //this.response.appendString("Call Number")
        //this.response.appendString("Timestamp")
        //this.response.appendString("Message")
        return this.response;
    }
}
