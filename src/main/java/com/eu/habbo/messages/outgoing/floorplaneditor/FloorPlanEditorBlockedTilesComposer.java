package com.eu.habbo.messages.outgoing.floorplaneditor;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;

public class FloorPlanEditorBlockedTilesComposer extends MessageComposer
{
    private final Room room;

    public FloorPlanEditorBlockedTilesComposer(Room room)
    {
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.FloorPlanEditorBlockedTilesComposer);

        THashSet<RoomTile> tileList = this.room.getLockedTiles();

        this.response.appendInt32(tileList.size());
        for(RoomTile node : tileList)
        {
            this.response.appendInt32((int) node.x);
            this.response.appendInt32((int) node.y);
        }

        return this.response;
    }
}
