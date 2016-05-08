package com.eu.habbo.messages.outgoing.hotelview;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HotelViewCommunityGoalComposer extends MessageComposer
{
    //:test 1579 b:1 i:0 i:1 i:2 i:3 i:4 i:5 s:a i:6 i:1 i:1
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HotelViewCommunityGoalComposer);
        this.response.appendBoolean(true); //Achieved?
        this.response.appendInt32(0); //User Amount
        this.response.appendInt32(0); //User Rank
        this.response.appendInt32(0); //Total Amount
        this.response.appendInt32(0); //Community Highest Achieved
        this.response.appendInt32(0); //Community Score Untill Next Level
        this.response.appendInt32(0); //Percent Completed Till Next Level
        this.response.appendString("competition_name");
        this.response.appendInt32(0); //Timer
        this.response.appendInt32(1); //Rank Count
        this.response.appendInt32(1); //Rank level
        return this.response;
    }
}
