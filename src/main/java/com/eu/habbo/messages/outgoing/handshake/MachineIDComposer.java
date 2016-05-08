package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 27-7-2015 12:06.
 */
public class MachineIDComposer extends MessageComposer
{
    private String machineID;

    public MachineIDComposer(String machineID)
    {
        this.machineID = machineID;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MachineIDComposer);
        this.response.appendString(this.machineID);
        return this.response;
    }
}
