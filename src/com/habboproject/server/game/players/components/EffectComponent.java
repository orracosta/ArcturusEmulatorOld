package com.habboproject.server.game.players.components;

import com.habboproject.server.game.players.components.types.inventory.InventoryEffect;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.network.messages.outgoing.user.inventory.EffectAddedMessageComposer;
import com.habboproject.server.storage.queries.effects.EffectDao;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by brend on 01/02/2017.
 */
public class EffectComponent {
    private Player player;
    private Map<Integer, InventoryEffect> effects;
    private Logger logger = Logger.getLogger(EffectComponent.class.getName());

    public EffectComponent(Player player) {
        this.player = player;
        this.effects = Maps.newConcurrentMap();
        this.loadEffects();
    }

    public Map<Integer, InventoryEffect> getEffects() {
        return this.effects;
    }

    public void loadEffects() {
        if (this.effects != null && this.effects.size() >= 1) {
            this.effects.clear();
        }

        try {
            this.effects = EffectDao.getEffectsByPlayerId(this.player.getId());
        }
        catch (Exception e) {
            this.logger.error("Error while loading user effects", e);
        }
    }

    public boolean hasEffect(int effectId) {
        if (effectId == 0) {
            return true;
        }

        for (InventoryEffect effect : this.effects.values()) {
            if (effect.getEffectId() != effectId) continue;
            return true;
        }

        return false;
    }

    public void addEffect(int effectId, int duration, boolean isTotem) {
        if (this.hasEffect(effectId)) {
            return;
        }
        int effect = EffectDao.addEffect(this.player.getId(), effectId, duration, isTotem);

        this.effects.put(effect, new InventoryEffect(effect, effectId, duration, isTotem, false, 0.0, 1));
        this.player.getSession().send(new EffectAddedMessageComposer(effectId, duration, isTotem));
    }

    public void dispose() {
        this.effects.clear();
        this.effects = null;
        this.player = null;
    }
}
