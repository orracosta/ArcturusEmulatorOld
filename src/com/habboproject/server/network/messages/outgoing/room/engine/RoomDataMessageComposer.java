package com.habboproject.server.network.messages.outgoing.room.engine;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.RoomWriter;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class RoomDataMessageComposer extends MessageComposer {
    private final Room room;
    private final boolean checkEntry;
    private final boolean canMute;
    private final boolean isLoading;

    public RoomDataMessageComposer(final Room room, boolean checkEntry, boolean canMute) {
        this.room = room;
        this.checkEntry = checkEntry;
        this.canMute = canMute;
        this.isLoading = false;
    }

    public RoomDataMessageComposer(final Room room, boolean checkEntry, boolean canMute, boolean isLoading) {
        this.room = room;
        this.checkEntry = checkEntry;
        this.canMute = canMute;
        this.isLoading = isLoading;
    }

    public RoomDataMessageComposer(final Room room) {
        this.room = room;
        this.checkEntry = true;
        this.canMute = false;
        this.isLoading = true;
    }

    @Override
    public short getId() {
        return Composers.GetGuestRoomResultMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        RoomWriter.entryData(room.getData(), msg, this.isLoading, this.checkEntry, false, this.canMute);
    }
}
