package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CameraPublishWaitMessageComposer extends MessageComposer
{
    public final boolean canBuy;
    public final int seconds;
    public final String unknownString;

    public CameraPublishWaitMessageComposer(boolean canBuy, int seconds, String unknownString)
    {
        this.canBuy = canBuy;
        this.seconds = seconds;
        this.unknownString = unknownString;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CameraPublishWaitMessageComposer);
        this.response.appendBoolean(this.canBuy);
        this.response.appendInt32(this.seconds);
        this.response.appendString(this.unknownString);
        return this.response;
    }
}