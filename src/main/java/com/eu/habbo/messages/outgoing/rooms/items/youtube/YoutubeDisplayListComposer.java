package com.eu.habbo.messages.outgoing.rooms.items.youtube;

import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;

public class YoutubeDisplayListComposer extends MessageComposer
{
    public final int itemId;
    public final ArrayList<YoutubeManager.YoutubeItem> items;

    public YoutubeDisplayListComposer(int itemId, ArrayList<YoutubeManager.YoutubeItem> items)
    {
        this.itemId = itemId;
        this.items = items;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.YoutubeDisplayListComposer);
        this.response.appendInt(this.itemId);
        this.response.appendInt(this.items.size());

        for (YoutubeManager.YoutubeItem item : this.items)
        {
            this.response.appendString(item.video);
            this.response.appendString(item.title);
            this.response.appendString(item.description);
        }

        this.response.appendString("");
        return this.response;
    }
}