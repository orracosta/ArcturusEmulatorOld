package com.habboproject.server.network.messages.outgoing.room.engine;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.game.rooms.types.tiles.RoomTileState;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class HeightmapMessageComposer extends MessageComposer {
    private final Room room;

    public HeightmapMessageComposer(final Room room) {
        this.room = room;
    }

    @Override
    public short getId() {
        return Composers.HeightMapMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(room.getModel().getSizeX());
        msg.writeInt(room.getModel().getSizeX() * room.getModel().getSizeY());

        for (int y = 0; y < room.getModel().getSizeY(); y++) {
            for (int x = 0; x < room.getModel().getSizeX(); x++) {
                RoomTile tile = room.getMapping().getTile(x, y);

                if (room.getModel().getSquareState()[x][y] == RoomTileState.INVALID) {
                    msg.writeShort(16191);
                } else if (room.getModel().getDoorY() == y && room.getModel().getDoorX() == x) {
                    msg.writeShort(0);
                } else {

                    if (tile != null) {
                        int height = (int) ((tile.getStackHeight(null)) * 256);

                        msg.writeShort(height);
                    } else {
                        msg.writeShort((short) room.getModel().getSquareHeight()[x][y] * 256);
                    }
                }
            }
        }
    }
}
