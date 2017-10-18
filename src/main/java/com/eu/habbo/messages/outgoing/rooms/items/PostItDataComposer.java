package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.items.PostItColor;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PostItDataComposer extends MessageComposer
{
    private InteractionPostIt postIt;
    private boolean hasRights;

    public PostItDataComposer(InteractionPostIt postIt, boolean hasRights)
    {
        this.postIt = postIt;
        this.hasRights = hasRights;
    }

    @Override
    public ServerMessage compose()
    {
        if(this.postIt.getExtradata().isEmpty() || this.postIt.getExtradata().length() < 6)
        {
            this.postIt.setExtradata("FFFF33");
        }

        this.response.init(Outgoing.PostItDataComposer);
        this.response.appendString(this.postIt.getId() + "");
        this.response.appendString(this.postIt.getExtradata());
        return this.response;
    }
}
