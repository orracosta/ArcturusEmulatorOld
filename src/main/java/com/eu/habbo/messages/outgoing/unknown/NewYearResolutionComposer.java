package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 4-11-2014 12:51.
 */
public class NewYearResolutionComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        //:test 817 i:230 i:1 i:1 i:1 s:NY2013RES i:3 i:0 i:60000000
        this.response.init(Outgoing.NewYearResolutionComposer);

        this.response.appendInt32(230); //reward ID or item id? (stuffId)
        this.response.appendInt32(2); //count

            this.response.appendInt32(1); //achievementId
            this.response.appendInt32(1); //achievementLevel
            this.response.appendString("NY2013RES");
            this.response.appendInt32(3); //type ?
            this.response.appendInt32(0); //Finished/ enabled

            this.response.appendInt32(2); //achievementId
            this.response.appendInt32(1); //achievementLevel
            this.response.appendString("ADM");
            this.response.appendInt32(2); //type ?
            this.response.appendInt32(0); //Finished/ enabled

        this.response.appendInt32(1000); //Time in secs left.

        return this.response;
    }
}
