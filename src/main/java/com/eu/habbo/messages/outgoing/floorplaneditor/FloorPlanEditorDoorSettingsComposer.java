package com.eu.habbo.messages.outgoing.floorplaneditor;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class FloorPlanEditorDoorSettingsComposer extends MessageComposer
{
    private final Room room;

    public FloorPlanEditorDoorSettingsComposer(Room room)
    {
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.FloorPlanEditorDoorSettingsComposer);
        this.response.appendInt32(this.room.getLayout().getDoorX());
        this.response.appendInt32(this.room.getLayout().getDoorY());
        this.response.appendInt(this.room.getLayout().getDoorDirection());
        return this.response;
    }
}
