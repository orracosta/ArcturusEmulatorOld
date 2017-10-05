package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.google.common.collect.Maps;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

import java.util.Map;

public class WiredActionGiveScore extends WiredActionItem {
    private static final int PARAM_SCORE = 0;
    private static final int PARAM_PER_GAME = 1;

    private final Map<Long, Integer> GAME_DATA;

    public WiredActionGiveScore(final long id, final int itemId, final Room room, final int owner, final int groupId, final int x, final int y, final double z, final int rotation, final String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        this.GAME_DATA = Maps.newHashMap();

        if (this.getWiredData().getParams().size() < 2) {
            this.getWiredData().getParams().clear();
            this.getWiredData().getParams().put(PARAM_SCORE, 1);
            this.getWiredData().getParams().put(PARAM_PER_GAME, 1);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 6;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        if (!(event.entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity playerEntity = (PlayerEntity)event.entity;
        if (playerEntity.getGameTeam() == null || playerEntity.getGameTeam() == GameTeam.NONE) {
            return;
        }

        this.getRoom().getGame().increaseScoreToPlayer(this.getId(), playerEntity, playerEntity.getGameTeam(), this.getPerGame(), this.getScore());
    }

    public int getScore() {
        return this.getWiredData().getParams().get(PARAM_SCORE);
    }

    public int getPerGame() {
        return this.getWiredData().getParams().get(PARAM_PER_GAME);
    }
}
