package com.eu.habbo.messages.outgoing.guardians;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuardianVotingTimeEnded extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuardianVotingTimeEnded);
        //Empty Body
        return this.response;
    }
}
