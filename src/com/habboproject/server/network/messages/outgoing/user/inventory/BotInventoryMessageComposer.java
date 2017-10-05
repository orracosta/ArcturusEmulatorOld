package com.habboproject.server.network.messages.outgoing.user.inventory;

import com.habboproject.server.api.game.players.data.components.bots.PlayerBot;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.Map;


public class BotInventoryMessageComposer extends MessageComposer {

    private final Map<Integer, PlayerBot> bots;

    public BotInventoryMessageComposer(final Map<Integer, PlayerBot> bots) {
        this.bots = bots;
    }

    public BotInventoryMessageComposer() {
        this.bots = null;
    }

    @Override
    public short getId() {
        return Composers.BotInventoryMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        if (this.bots == null) {
            msg.writeInt(0);
            return;
        }

        msg.writeInt(bots.size());

        for (Map.Entry<Integer, PlayerBot> bot : bots.entrySet()) {
            msg.writeInt(bot.getKey());
            msg.writeString(bot.getValue().getName());
            msg.writeString(bot.getValue().getMotto());
            msg.writeString(bot.getValue().getGender());
            msg.writeString(bot.getValue().getFigure());
        }
    }
}
