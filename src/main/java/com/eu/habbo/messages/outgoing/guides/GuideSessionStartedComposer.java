package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuideSessionStartedComposer extends MessageComposer
{
    private final GuideTour tour;

    public GuideSessionStartedComposer(GuideTour tour)
    {
        this.tour = tour;
    }

    @Override
    public ServerMessage compose()
    {
        //:test 3048 i:1 s:Admin s:b i:2 s:Derp s:d
        this.response.init(Outgoing.GuideSessionStartedComposer);

        //Help Requester
        this.response.appendInt(this.tour.getNoob().getHabboInfo().getId()); //UserID 1?
        this.response.appendString(this.tour.getNoob().getHabboInfo().getUsername()); //UserName 1?
        this.response.appendString(this.tour.getNoob().getHabboInfo().getLook()); //Look 1?

        //Being helped by.
        this.response.appendInt(this.tour.getHelper().getHabboInfo().getId()); //UserID 2?
        this.response.appendString(this.tour.getHelper().getHabboInfo().getUsername()); //UserName 2?
        this.response.appendString(this.tour.getHelper().getHabboInfo().getLook()); //Look 2?
        return this.response;
    }
}
