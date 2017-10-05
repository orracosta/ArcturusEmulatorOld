package com.habboproject.server.network.messages.outgoing.room.trading;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class TradeConfirmationMessageComposer extends MessageComposer {

    @Override
    public short getId() {
        return Composers.TradingCompleteMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {

    }
}
