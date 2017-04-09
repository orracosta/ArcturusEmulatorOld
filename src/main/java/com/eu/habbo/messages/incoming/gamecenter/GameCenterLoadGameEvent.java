package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.gamcenter.basejump.BaseJumpLeaveQueueComposer;
import com.eu.habbo.messages.outgoing.gamcenter.basejump.BaseJumpLoadGameComposer;
import com.eu.habbo.messages.outgoing.gamcenter.basejump.BaseJumpLoadGameURLComposer;

public class GameCenterLoadGameEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int gameId = this.packet.readInt();

        if (gameId == 3)
        {
        }
    }
}