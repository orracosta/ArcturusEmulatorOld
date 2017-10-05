package com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;


public class WiredTriggerScoreAchieved extends WiredTriggerItem {
    private static final int PARAM_SCORE_TO_ACHIEVE = 0;

    public WiredTriggerScoreAchieved(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean suppliesPlayer() {
        return true;
    }

    @Override
    public int getInterface() {
        return 10;
    }

    public int scoreToAchieve() {
        if (this.getWiredData().getParams().size() == 1) {
            return this.getWiredData().getParams().get(PARAM_SCORE_TO_ACHIEVE);
        }

        return 0;
    }

    public static boolean executeTriggers(int score, GameTeam team, Room room, PlayerEntity playerEntity) {
        boolean wasExecuted = false;

        for (RoomItemFloor floorItem : room.getItems().getByClass(WiredTriggerScoreAchieved.class)) {
            WiredTriggerScoreAchieved trigger = ((WiredTriggerScoreAchieved) floorItem);

            if (trigger.scoreToAchieve() >= score) {
                wasExecuted = trigger.evaluate(playerEntity, team);
            }
        }

        return wasExecuted;
    }
}
