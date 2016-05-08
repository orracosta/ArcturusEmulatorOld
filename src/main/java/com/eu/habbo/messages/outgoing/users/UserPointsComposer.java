package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 14-2-2015 13:37.
 */
public class UserPointsComposer extends MessageComposer
{
    private final int currentAmount;
    private final int amountAdded;
    private final int type;

    public UserPointsComposer(int currentAmount, int amountAdded, int type)
    {
        this.currentAmount = currentAmount;
        this.amountAdded = amountAdded;
        this.type = type;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UserPointsComposer);
        this.response.appendInt32(this.currentAmount);
        this.response.appendInt32(this.amountAdded);
        this.response.appendInt32(this.type);
        return this.response;
    }
}
