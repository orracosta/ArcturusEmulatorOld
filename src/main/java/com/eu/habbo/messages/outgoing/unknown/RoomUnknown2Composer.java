package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RoomUnknown2Composer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUnknown2Composer);
        this.response.appendInt(0); //Count
        {
            this.response.appendString(""); //Name
            this.response.appendInt(0); //Target

            this.response.appendInt(0); //Count
            {//Part of previous object (string, int)
                this.response.appendString("");
                this.response.appendInt(0);
            }
        }
        return this.response;
    }
}