package com.habboproject.server.game.effects;

import com.habboproject.server.game.effects.types.EffectItem;
import com.habboproject.server.storage.queries.effects.EffectDao;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by brend on 01/02/2017.
 */
public class EffectManager {
    public static EffectManager instance;
    private final Logger log = Logger.getLogger(EffectManager.class.getName());
    private final Map<Integer, EffectItem> effects = Maps.newConcurrentMap();

    public EffectManager() {
    }

    public static EffectManager getInstance() {
        if (instance == null)
            instance = new EffectManager();

        return instance;
    }

    public void initialize() {
        if (effects != null && effects.size() > 0)
            effects.clear();

        try {
            EffectDao.loadEffectsPermissions(effects);
        } catch (Exception e) {
            log.error("Error while loading effects permissions", e);
        }

        log.info((new StringBuilder("Loaded ")).append(effects.size()).append(" effects permissions").toString());
    }

    public Map<Integer, EffectItem> getEffects() {
        return effects;
    }
}
