package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 14-7-2015 12:54.
 */
public class ModToolReportReceivedAlertComposer extends MessageComposer
{
    public static final int REPORT_RECEIVED = 0;
    public static final int REPORT_WINDOW = 1;
    public static final int REPORT_ABUSIVE = 2;

    private int errorCode;

    public ModToolReportReceivedAlertComposer(int errorCode)
    {
        this.errorCode = errorCode;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolReportReceivedAlertComposer);
        this.response.appendInt32(this.errorCode);
        return null;
    }
}
