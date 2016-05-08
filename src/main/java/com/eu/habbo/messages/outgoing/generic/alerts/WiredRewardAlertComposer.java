package com.eu.habbo.messages.outgoing.generic.alerts;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 3-11-2014 21:39.
 */
public class WiredRewardAlertComposer extends MessageComposer
{
    public static final int LIMITED_NO_MORE_AVAILABLE = 0;
    public static final int REWARD_ALREADY_RECEIVED = 1;
    public static final int REWARD_ALREADY_RECEIVED_THIS_TODAY = 2;
    public static final int REWARD_ALREADY_RECEIVED_THIS_HOUR = 3;
    public static final int REWARD_ALREADY_RECEIVED_THIS_MINUTE = 8;
    public static final int UNLUCKY_NO_REWARD = 4;
    public static final int REWARD_ALL_COLLECTED = 5;
    public static final int REWARD_RECEIVED_ITEM = 6;
    public static final int REWARD_RECEIVED_BADGE = 7;

    private final int code;

    public WiredRewardAlertComposer(int code)
    {
        this.code = code;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.WiredRewardAlertComposer);
        this.response.appendInt32(this.code);
        return this.response;
    }
}
