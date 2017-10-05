package com.habboproject.server.game.rooms.objects.items.types.floor.wired.conditions.positive;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredConditionItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;

/**
 * Created by brend on 12/03/2017.
 */
public class WiredConditionActorInTeam extends WiredConditionItem {
    private final int PARAM_SELECTED_TEAM = 0;

    /**
     * The default constructor
     *
     * @param id       The ID of the item
     * @param itemId   The ID of the item definition
     * @param room     The instance of the room
     * @param owner    The ID of the owner
     * @param x        The position of the item on the X axis
     * @param y        The position of the item on the Y axis
     * @param z        The position of the item on the z axis
     * @param rotation The orientation of the item
     * @param data     The JSON object associated with this item
     */
    public WiredConditionActorInTeam(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public int getInterface() {
        return 6;
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        if (entity == null || !(entity instanceof PlayerEntity)) {
            return false;
        }

        if (this.getWiredData().getParams() == null || this.getWiredData().getParams().size() == 0 || this.getWiredData().getParams().get(PARAM_SELECTED_TEAM) == null) {
            return false;
        }

        final int selectedTeam = this.getWiredData().getParams().get(PARAM_SELECTED_TEAM);

        if (selectedTeam <= 0 || selectedTeam > 4) {
            return false;
        }

        if (selectedTeam == 1 && ((PlayerEntity) entity).getGameTeam() == GameTeam.RED) {
            if (!isNegative) {
                return true;
            } else {
                return false;
            }
        }

        else if (selectedTeam == 2 && ((PlayerEntity) entity).getGameTeam() == GameTeam.GREEN) {
            if (!isNegative) {
                return true;
            } else {
                return false;
            }
        }

        else if (selectedTeam == 3 && ((PlayerEntity) entity).getGameTeam() == GameTeam.BLUE) {
            if (!isNegative) {
                return true;
            } else {
                return false;
            }
        }

        else if (selectedTeam == 4 && ((PlayerEntity) entity).getGameTeam() == GameTeam.YELLOW) {
            if (!isNegative) {
                return true;
            } else {
                return false;
            }
        }

        return isNegative ? true : false;
    }
}
