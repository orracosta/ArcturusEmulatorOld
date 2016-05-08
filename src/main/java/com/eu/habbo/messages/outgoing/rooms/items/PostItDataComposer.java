package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.items.PostItColor;
import com.eu.habbo.habbohotel.items.interactions.InteractionPostIt;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 9-7-2015 17:26.
 */
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

        String color = this.postIt.getExtradata().split(" ")[0];
        String text = this.postIt.getExtradata().replace(this.postIt.getExtradata().split(" ")[0], "").replace((char) 9 + "", "");

        if(hasRights && PostItColor.isCustomColor(color))
        {
            text = color + " " + text;
        }

        this.response.init(Outgoing.PostItDataComposer);
        this.response.appendString(this.postIt.getId() + "");
        this.response.appendString(color + "\t" + text);
        return this.response;
    }
}
