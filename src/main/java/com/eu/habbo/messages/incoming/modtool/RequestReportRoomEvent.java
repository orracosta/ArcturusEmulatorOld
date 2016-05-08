package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ReportRoomFormComposer;

/**
 * Created on 3-3-2015 13:17.
 */
public class RequestReportRoomEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new ReportRoomFormComposer());
    }
}
