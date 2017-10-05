package com.habboproject.server.network.messages.outgoing.gamecenter;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 08/02/2017.
 */
public class GameListMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return 3108;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(1);

        msg.writeInt(3);
        msg.writeString("basejump");
        msg.writeString("68bbd2");
        msg.writeString("");
        msg.writeString(Comet.getServer().getConfig().get("comet.gamecenter.image.path") + "/gamecenter_basejump/");
        msg.writeString("");
    }
}
