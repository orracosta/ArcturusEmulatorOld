package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.items.NewUserGift;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;
import java.util.Map;

public class NewUserGiftComposer extends MessageComposer
{
    private final List<List<NewUserGift>> options;

    public NewUserGiftComposer(List<List<NewUserGift>> options)
    {
        this.options = options;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewUserGiftComposer);
        this.response.appendInt(this.options.size());
        for (List<NewUserGift> option : this.options)
        {
            this.response.appendInt(1);
            this.response.appendInt(3);
            this.response.appendInt(option.size());
            for (NewUserGift gift : option)
            {
                gift.serialize(this.response);
            }
        }
        return this.response;
    }
}