package com.habboproject.server.network.messages.outgoing.user.inventory;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.players.components.types.inventory.InventoryEffect;
import com.habboproject.server.network.messages.composers.MessageComposer;

import java.util.Collection;


public class EffectsInventoryMessageComposer extends MessageComposer {
    private final Collection<InventoryEffect> effects;

    public EffectsInventoryMessageComposer(Collection<InventoryEffect> effects) {
        this.effects = effects;
    }

    @Override
    public short getId() {
        return 3940;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.effects.size());
        for (InventoryEffect effect : this.effects) {
            msg.writeInt(effect.getEffectId());
            msg.writeInt(0);
            msg.writeInt((int)effect.getDuration());
            msg.writeInt((effect.getQuantity() - 1));
            msg.writeInt((int)(Comet.getTime() - effect.getTimestampActivated()));
            msg.writeBoolean(effect.isTotem());
        }
    }
}
