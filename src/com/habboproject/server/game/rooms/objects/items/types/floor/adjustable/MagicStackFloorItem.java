package com.habboproject.server.game.rooms.objects.items.types.floor.adjustable;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;

import java.text.DecimalFormat;


public class MagicStackFloorItem extends RoomItemFloor {
    private double magicHeight = 0.0;

    public MagicStackFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (!data.isEmpty()) {
            this.magicHeight = Double.parseDouble(data);
        }
    }

    @Override
    public String getDataObject() {
        return (this.magicHeight == 0.0 ? "" : new DecimalFormat("#.00").format(magicHeight).replace(",", "."));
    }

    @Override
    public void onPlaced() {
        this.magicHeight = 0.0;
        this.saveData();
    }

    public double getMagicHeight() {
        RoomTile tile = this.getRoom().getMapping().getTile(this.getPosition().copy());
        if (tile != null) {
            return tile.getTileHeight() + this.magicHeight;
        }

        return this.magicHeight;
    }

    public void setMagicHeight(double magicHeight) {
        this.setExtraData(new DecimalFormat("#.00").format(magicHeight).replace(",", "."));
        this.magicHeight = magicHeight;
    }
}
