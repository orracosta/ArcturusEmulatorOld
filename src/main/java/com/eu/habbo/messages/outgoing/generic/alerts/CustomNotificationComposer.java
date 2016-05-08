package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 2-8-2015 16:40.
 */
public class CustomNotificationComposer extends MessageComposer
{
    public static final int HOPPER_NO_COSTUME = 1;
    public static final int HOPPER_NO_HC = 2;
    public static final int GATE_NO_HC = 3;
    public static final int STARS_NOT_CANDIDATE = 4;
    public static final int STARS_NOT_ENOUGH_USERS = 5;

    private int type;

    public CustomNotificationComposer(int type)
    {
        this.type = type;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CustomNotificationComposer);
        this.response.appendInt32(this.type);
        return this.response;
    }
}
