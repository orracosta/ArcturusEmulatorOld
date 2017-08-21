package com.eu.habbo.messages.incoming.gamecenter;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.gamcenter.GameCenterAccountInfoComposer;
import com.eu.habbo.messages.outgoing.gamcenter.GameCenterAchievementsConfigurationComposer;

public class GameCenterRequestGamesEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new GameCenterAchievementsConfigurationComposer());
    }
}