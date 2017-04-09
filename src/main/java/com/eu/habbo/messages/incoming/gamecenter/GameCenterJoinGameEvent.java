package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.gamcenter.GameCenterAccountInfoComposer;
import com.eu.habbo.messages.outgoing.gamcenter.GameCenterAchievementsConfigurationComposer;
import com.eu.habbo.messages.outgoing.gamcenter.basejump.BaseJumpJoinQueueComposer;
import com.eu.habbo.messages.outgoing.gamcenter.basejump.BaseJumpLeaveQueueComposer;
import com.eu.habbo.messages.outgoing.gamcenter.basejump.BaseJumpLoadGameComposer;
import com.eu.habbo.messages.outgoing.gamcenter.basejump.BaseJumpLoadGameURLComposer;

public class GameCenterJoinGameEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int gameId = this.packet.readInt();

        if (gameId == 3) //BaseJump
        {
            this.client.sendResponse(new GameCenterAchievementsConfigurationComposer());
            this.client.sendResponse(new BaseJumpLoadGameComposer(this.client));
            //this.client.sendResponse(new BaseJumpLoadGameURLComposer());
        }
    }
}