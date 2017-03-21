package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.gamcenter.GameCenterAccountInfoComposer;

public class GameCenterRequestAccountStatusEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new GameCenterAccountInfoComposer());
    }
}