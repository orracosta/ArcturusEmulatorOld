package com.habboproject.server.network.messages.outgoing.room.settings;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class SettingsUpdatedMessageComposer extends MessageComposer {

    private final int roomId;

    public SettingsUpdatedMessageComposer(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public short getId() {
        return Composers.RoomSettingsSavedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.roomId);
    }
}
