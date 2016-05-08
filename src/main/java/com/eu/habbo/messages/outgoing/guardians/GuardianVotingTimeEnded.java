package com.eu.habbo.messages.outgoing.guardians;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 11-10-2015 16:44.
 */
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
