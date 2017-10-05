package com.habboproject.server.game.rooms.objects.items.types.floor.others;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;

/**
 * Created by brend on 04/02/2017.
 */
public class BadgeDisplayFloorItem extends RoomItemFloor {
    private String badge;
    private String name;
    private String date;

    public BadgeDisplayFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        this.configure();
    }

    @Override
    public void compose(IComposer msg, boolean isNew) {
        if (badge.isEmpty()) {
            this.configure();
        }

        msg.writeInt(0);
        msg.writeInt(2);
        msg.writeInt(4);
        msg.writeString("0");
        msg.writeString(badge);
        msg.writeString(name);
        msg.writeString(date);
    }

    private void configure() {
        if (this.getExtraData().contains("~")) {
            String[] params = this.getExtraData().split("~");
            badge = params[0];
            name = params[1];
            date = params[2];
        } else {
            badge = this.getExtraData();
            name = "";
            date = "";
        }
    }
}
