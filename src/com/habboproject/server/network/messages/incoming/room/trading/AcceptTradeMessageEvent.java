package com.habboproject.server.network.messages.incoming.room.trading;

import com.habboproject.server.game.rooms.types.components.types.Trade;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class AcceptTradeMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if(client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        Trade trade = client.getPlayer().getEntity().getRoom().getTrade().get(client.getPlayer().getEntity());

        if (trade == null) {
            return;
        }

        trade.accept(trade.getUserNumber(client.getPlayer().getEntity()));
    }
}
