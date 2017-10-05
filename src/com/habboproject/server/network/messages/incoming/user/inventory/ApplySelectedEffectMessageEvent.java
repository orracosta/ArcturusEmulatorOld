package com.habboproject.server.network.messages.incoming.user.inventory;

import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 01/02/2017.
 */
public class ApplySelectedEffectMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int effectId = msg.readInt();

        if (effectId < 0) {
            effectId = 0;
        }

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null &&
                client.getPlayer().getEffectComponent().hasEffect(effectId)) {
            client.getPlayer().getEntity().applyEffect(new PlayerEffect(effectId, 0));
        }
    }
}
