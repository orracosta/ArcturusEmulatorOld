package com.habboproject.server.game.players.components;

import com.habboproject.server.game.players.components.types.messenger.RelationshipLevel;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.players.types.PlayerComponent;
import com.habboproject.server.storage.queries.player.relationships.RelationshipDao;

import java.util.Map;


public class RelationshipComponent implements PlayerComponent {
    private Player player;
    private Map<Integer, RelationshipLevel> relationships;

    public RelationshipComponent(Player player) {
        this.player = player;

        this.relationships = RelationshipDao.getRelationshipsByPlayerId(player.getId());
    }

    public void dispose() {
        this.relationships.clear();
        this.relationships = null;
        this.player = null;
    }

    public RelationshipLevel get(int playerId) {
        return this.relationships.get(playerId);
    }

    public void remove(int playerId) {
        this.getRelationships().remove(playerId);
    }

    public int count() {
        return this.relationships.size();
    }

    public Map<Integer, RelationshipLevel> getRelationships() {
        return this.relationships;
    }

    public Player getPlayer() {
        return this.player;
    }

    public static int countByLevel(RelationshipLevel level, Map<Integer, RelationshipLevel> relationships) {
        int levelCount = 0;

        for (RelationshipLevel relationship : relationships.values()) {
            if (relationship == level) levelCount++;
        }

        return levelCount;
    }
}
