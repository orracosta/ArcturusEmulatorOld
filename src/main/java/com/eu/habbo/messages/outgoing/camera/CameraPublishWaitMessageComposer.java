package com.eu.habbo.messages.outgoing.camera;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class CameraPublishWaitMessageComposer extends MessageComposer
{
    public final boolean published;
    public final int seconds;
    public final String unknownString;

    public CameraPublishWaitMessageComposer(boolean published, int seconds, String unknownString)
    {
        this.published = published;
        this.seconds = seconds;
        this.unknownString = unknownString;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CameraPublishWaitMessageComposer);
        this.response.appendBoolean(this.published);
        this.response.appendInt(this.seconds);

        if (this.published)
        {
            this.response.appendString(this.unknownString);
        }
        return this.response;
    }
}