package com.habboproject.server.network.messages.incoming.room.trading;

import com.habboproject.server.game.rooms.types.components.types.Trade;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ConfirmTradeMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        Trade trade = client.getPlayer().getEntity().getRoom().getTrade().get(client.getPlayer().getEntity());

        if (trade == null) {
            return;
        }

        trade.confirm(trade.getUserNumber(client.getPlayer().getEntity()));
    }
}
