package com.habboproject.server.network.messages.outgoing.gamecenter;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 03/03/2017.
 */
public class GameAchievementsMessageComposer extends MessageComposer {
    //TODO: this

    @Override
    public short getId() {
        return 3856;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(0);
    }
}