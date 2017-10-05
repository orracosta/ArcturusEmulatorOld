package com.habboproject.server.network.messages.outgoing.room.trading;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class TradeAcceptUpdateMessageComposer extends MessageComposer {
    private final int playerId;
    private final boolean accepted;

    public TradeAcceptUpdateMessageComposer(int playerId, boolean accepted) {
        this.playerId = playerId;
        this.accepted = accepted;
    }

    @Override
    public short getId() {
        return Composers.TradingAcceptMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);
        msg.writeInt(accepted ? 1 : 0);
    }
}
