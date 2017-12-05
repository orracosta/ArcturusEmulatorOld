package com.eu.habbo.messages.outgoing.gamecenter;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GameCenterAccountInfoComposer extends MessageComposer
{
    private final int gameId;
    private final int gamesLeft;

    public GameCenterAccountInfoComposer(int gameId, int gamesLeft)
    {
        this.gameId = gameId;
        this.gamesLeft = gamesLeft;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GameCenterAccountInfoComposer);
        this.response.appendInt(this.gameId);
        this.response.appendInt(this.gamesLeft);
        this.response.appendInt(1);
        return this.response;
    }
}