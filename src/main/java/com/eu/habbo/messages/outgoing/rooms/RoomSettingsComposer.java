package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 6-9-2014 14:32.
 */
public class RoomSettingsComposer extends MessageComposer {

    private final Room room;

    public RoomSettingsComposer(Room room)
    {
        this.room = room;
    }
    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomSettingsComposer); //Updated
        this.response.appendInt32(this.room.getId());
        this.response.appendString(this.room.getName());
        this.response.appendString(this.room.getDescription());
        this.response.appendInt32(this.room.getState().getState());
        this.response.appendInt32(this.room.getCategory());
        this.response.appendInt32(this.room.getUsersMax());
        this.response.appendInt32(this.room.getUsersMax());

        this.response.appendInt32(this.room.getTags().split(";").length);
        for (String tag : this.room.getTags().split(";")) {
            this.response.appendString(tag);
        }
        //this.response.appendInt32(this.room.getRights().size());
        this.response.appendInt32(2); //Trade Mode
        this.response.appendInt32(this.room.isAllowPets() ? 1 : 0);
        this.response.appendInt32(this.room.isAllowPetsEat() ? 1 : 0);
        this.response.appendInt32(this.room.isAllowWalkthrough() ? 1 : 0);
        this.response.appendInt32(this.room.isHideWall() ? 1 : 0);
        this.response.appendInt32(this.room.getWallSize());
        this.response.appendInt32(this.room.getFloorSize());

        this.response.appendInt32(this.room.getChatMode());
        this.response.appendInt32(this.room.getChatWeight());
        this.response.appendInt32(this.room.getChatSpeed());
        this.response.appendInt32(this.room.getChatDistance());
        this.response.appendInt32(this.room.getChatProtection());

        this.response.appendBoolean(false); //IDK?

        this.response.appendInt32(this.room.getMuteOption());
        this.response.appendInt32(this.room.getKickOption());
        this.response.appendInt32(this.room.getBanOption());
        return this.response;
    }
}
