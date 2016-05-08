package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.games.freeze.FreezeGamePlayer;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 1-2-2015 16:43.
 */
public class FreezeLivesComposer extends MessageComposer
{
    private final FreezeGamePlayer gamePlayer;

    public FreezeLivesComposer(FreezeGamePlayer gamePlayer)
    {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.FreezeLivesComposer);
        this.response.appendInt32(this.gamePlayer.getHabbo().getHabboInfo().getId());
        this.response.appendInt32(this.gamePlayer.getLives());
        return this.response;
    }
}
