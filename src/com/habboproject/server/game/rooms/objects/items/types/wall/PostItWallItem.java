package com.habboproject.server.game.rooms.objects.items.types.wall;

import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.types.Room;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;


public class PostItWallItem extends RoomItemWall {
    private String colour;
    private String message;

    public PostItWallItem(long id, int itemId, Room room, int owner, String position, String data) {
        super(id, itemId, room, owner, position, data);

        if (this.isValidData(data))
            this.setExtraData(data);
        else
            this.setExtraData("FFFF33 ");
    }

    @Override
    public String getExtraData() {
        return colour;
    }

    @Override
    public void setExtraData(String extraData) {
        String[] data = extraData.split(" ");
        String colour = data[0];

        if (!this.isValidColour(colour)) {
            return;
        }

        super.setExtraData(extraData);

        this.colour = colour;

        this.message = StringUtils.join(Arrays.copyOfRange(data, 1, data.length), " ");
    }

    private boolean isValidColour(String colour) {
        switch (colour) {
            default:
                return false;

            case "FFFF33":
            case "FF9CFF":
            case "9CCEFF":
            case "9CFF9C":
                return true;
        }
    }

    private boolean isValidData(String data) {
        return data.contains(" ");
    }

    public String getColour() {
        return colour;
    }

    public String getMessage() {
        return message;
    }
}
