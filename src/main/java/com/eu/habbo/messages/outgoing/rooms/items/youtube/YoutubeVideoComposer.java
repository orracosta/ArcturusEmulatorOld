package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class YoutubeVideoComposer extends MessageComposer
{
    public final int itemId;
    public final YoutubeManager.YoutubeItem item;

    public YoutubeVideoComposer(int itemId, YoutubeManager.YoutubeItem item)
    {
        this.itemId = itemId;
        this.item = item;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.YoutubeMessageComposer2);
        this.response.appendInt(this.itemId);
        this.response.appendString(this.item.video);
        this.response.appendInt(this.item.startTime);
        this.response.appendInt(this.item.endTime);
        this.response.appendInt(0);
        return this.response;
    }
}