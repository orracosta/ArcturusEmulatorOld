package com.habboproject.server.network.messages.outgoing.room.settings;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class MuteAllInRoomMessageComposer extends MessageComposer {

    private final boolean roomHasMute;

    public MuteAllInRoomMessageComposer(boolean roomHasMute) {
        this.roomHasMute = roomHasMute;
    }

    @Override
    public short getId() {
        return Composers.MuteAllInRoomMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(this.roomHasMute);
    }
}
