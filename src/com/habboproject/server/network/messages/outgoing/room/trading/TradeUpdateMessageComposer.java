package com.habboproject.server.network.messages.outgoing.room.trading;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.players.components.types.inventory.InventoryItem;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.Set;


public class TradeUpdateMessageComposer extends MessageComposer {

    private final int user1;
    private final int user2;
    private final Set<PlayerItem> items1;
    private final Set<PlayerItem> items2;

    public TradeUpdateMessageComposer(int user1, int user2, Set<PlayerItem> items1, Set<PlayerItem> items2) {
        this.user1 = user1;
        this.user2 = user2;
        this.items1 = items1;
        this.items2 = items2;
    }

    @Override
    public short getId() {
        return Composers.TradingUpdateMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(user1);
        msg.writeInt(items1.size());

        for (PlayerItem item : items1) {
            ((InventoryItem) item).serializeTrade(msg);
        }

        msg.writeInt(0);
        msg.writeInt(0);

        msg.writeInt(user2);
        msg.writeInt(items2.size());

        for (PlayerItem item : items2) {
            ((InventoryItem) item).serializeTrade(msg);
        }

        msg.writeInt(0);
        msg.writeInt(0);
    }
}
