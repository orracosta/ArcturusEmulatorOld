package com.eu.habbo.messages.outgoing.floorplaneditor;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.util.pathfinding.Node;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;

/**
 * Created on 3-4-2015 22:46.
 */
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

        THashSet<Tile> tileList = this.room.getLockedTiles();

        this.response.appendInt32(tileList.size());
        for(Tile node : tileList)
        {
            this.response.appendInt32((int) node.getX());
            this.response.appendInt32((int) node.getY());
        }

        return this.response;
    }
}
