package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.hotelview.NewsList;
import com.eu.habbo.habbohotel.hotelview.NewsWidget;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class NewsListComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewsWidgetsComposer);
        NewsList newsList = Emulator.getGameEnvironment().getHotelViewManager().getNewsList();

        this.response.appendInt(newsList.getNewsWidgets().size());

        for(NewsWidget widget : newsList.getNewsWidgets())
        {
            this.response.appendInt(widget.getId());
            this.response.appendString(widget.getTitle());
            this.response.appendString(widget.getMessage());
            this.response.appendString(widget.getButtonMessage());
            this.response.appendInt(widget.getType());
            this.response.appendString("event:" + widget.getLink());
            this.response.appendString(widget.getImage());
        }

        return this.response;
    }
}
