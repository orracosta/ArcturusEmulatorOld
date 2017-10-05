package com.habboproject.server.network.messages.outgoing.room.engine;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 03/03/2017.
 */
public class SaveRoomThumbnailMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return 988;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(true);
        msg.writeBoolean(false);
    }
}
