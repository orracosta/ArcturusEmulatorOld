package com.habboproject.server.network.messages.outgoing.room.trading;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class TradeCloseMessageComposer extends MessageComposer {
    private final int playerId;

    public TradeCloseMessageComposer(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.TradingClosedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);
        msg.writeInt(2);
    }
}
