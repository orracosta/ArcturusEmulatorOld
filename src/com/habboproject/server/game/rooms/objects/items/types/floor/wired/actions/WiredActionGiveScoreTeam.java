package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

/**
 * Created by brend on 03/02/2017.
 */
public class WiredActionGiveScoreTeam extends WiredActionItem {
    private static final int PARAM_SCORE = 0;
    private static final int PARAM_PER_GAME = 1;
    private static final int PARAM_TEAM = 2;

    public WiredActionGiveScoreTeam(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getWiredData().getParams().size() < 3) {
            this.getWiredData().getParams().clear();
            this.getWiredData().getParams().put(PARAM_SCORE, 1);
            this.getWiredData().getParams().put(PARAM_PER_GAME, 1);
            this.getWiredData().getParams().put(PARAM_TEAM, 1);
        }
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 14;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (this.getTeamById() == null || this.getRoom().getGame() == null) {
            return;
        }

        this.getRoom().getGame().increaseScoreToTeam(this.getItemId(), this.getTeamById(), this.getPerGame(), this.getScore());
    }

    public int getScore() {
        return this.getWiredData().getParams().get(PARAM_SCORE);
    }

    public int getPerGame() {
        return this.getWiredData().getParams().get(PARAM_PER_GAME);
    }

    public int getTeam() {
        return this.getWiredData().getParams().get(PARAM_TEAM);
    }

    public GameTeam getTeamById() {
        switch (this.getTeam()) {
            case 1: {
                return GameTeam.RED;
            }
            case 2: {
                return GameTeam.GREEN;
            }
            case 3: {
                return GameTeam.BLUE;
            }
            case 4: {
                return GameTeam.YELLOW;
            }
            default: {
                return null;
            }
        }
    }
}
