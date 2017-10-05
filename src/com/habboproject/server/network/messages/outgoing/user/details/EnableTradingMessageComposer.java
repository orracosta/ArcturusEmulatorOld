package com.habboproject.server.network.messages.outgoing.user.details;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;


public class EnableTradingMessageComposer extends MessageComposer {

    private final boolean tradingEnabled;

    public EnableTradingMessageComposer(final boolean tradingEnabled) {
        this.tradingEnabled = tradingEnabled;
    }

    @Override
    public short getId() {
        return 0;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(this.tradingEnabled);
    }
}
