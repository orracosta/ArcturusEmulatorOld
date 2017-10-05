package com.habboproject.server.game.rooms.objects.items.types.floor.toner;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.data.BackgroundTonerData;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;


public class BackgroundTonerFloorItem extends RoomItemFloor {
    public BackgroundTonerFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void compose(IComposer msg, boolean isNew) {
        BackgroundTonerData data = BackgroundTonerData.get(this.getExtraData());

        boolean enabled = (data != null);

        msg.writeInt(0);
        msg.writeInt(5);
        msg.writeInt(4);
        msg.writeInt(enabled ? 1 : 0);

        if (enabled) {
            msg.writeInt(data.getHue());
            msg.writeInt(data.getSaturation());
            msg.writeInt(data.getLightness());
        } else {
            this.setExtraData("0;#;0;#;0");
            this.saveData();

            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
        }
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        this.setExtraData("");
        this.saveData();

        this.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(this));

        return true;
    }
}
