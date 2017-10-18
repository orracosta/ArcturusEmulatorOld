package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ClubCenterDataComposer extends MessageComposer
{
    public final int streakDuration;
    public final String joinDate;
    public final double percentage;
    public final int creditsSpend;
    public final int creditsBonus;
    public final int spendBonus;
    public final int delay;

    public ClubCenterDataComposer(int streakDuration, String joinDate, double percentage, int creditsSpend, int creditsBonus, int spendBonus, int delay)
    {
        this.streakDuration = streakDuration;
        this.joinDate = joinDate;
        this.percentage = percentage;
        this.creditsSpend = creditsSpend;
        this.creditsBonus = creditsBonus;
        this.spendBonus = spendBonus;
        this.delay = delay;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ClubCenterDataComposer);
        this.response.appendInt(this.streakDuration); //streakduration in days
        this.response.appendString(this.joinDate); //joindate
        this.response.appendDouble(this.percentage); //percentage
        this.response.appendInt(0); //Unused
        this.response.appendInt(0); //unused
        this.response.appendInt(this.creditsSpend); //Amount credits spend
        this.response.appendInt(this.creditsBonus); //Credits bonus
        this.response.appendInt(this.spendBonus); //Spend bonus
        this.response.appendInt(this.delay); //next pay in minutes
        return this.response;
    }
}