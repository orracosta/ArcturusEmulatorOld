package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ModToolSanctionInfoComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolSanctionInfoComposer);
        this.response.appendBoolean(false); //Has Last Sanction.
        this.response.appendBoolean(false); //Is probabtion.
        this.response.appendString("<< Last Sanction >>");
        this.response.appendInt(0); //Value | Probation days left.
        this.response.appendInt(-1); //Unused
        this.response.appendString("<< Reason >>"); //Reason
        this.response.appendString("1/1/1970 00:00"); //Start Time
        this.response.appendInt(0); //Probation Days Left
        this.response.appendString("<< Next Sanction >>"); //Next Sanction
        this.response.appendInt(0); //Value
        this.response.appendInt(-1); //Unused
        this.response.appendBoolean(false); //Trade Locked
        this.response.appendString("1/1/1970 00:00"); //Trade Locked Untill

        return this.response;
    }
}