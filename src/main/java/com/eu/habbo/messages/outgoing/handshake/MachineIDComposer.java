package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MachineIDComposer extends MessageComposer
{
    private final GameClient client;

    public MachineIDComposer(GameClient client)
    {
        this.client = client;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MachineIDComposer);
        this.response.appendString(this.client.getMachineId());
        return this.response;
    }
}
