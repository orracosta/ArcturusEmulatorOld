package com.eu.habbo.messages.outgoing.gamcenter;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GameCenterAchievementsConfigurationComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(2265);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
            this.response.appendInt32(3);
            this.response.appendInt32(1);
                this.response.appendInt32(1);
                this.response.appendString("BaseJumpBigParachute");
                this.response.appendInt32(1);
        return this.response;
    }
}