package com.habboproject.server.network.messages.outgoing.user.details;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

/**
 * Created by brend on 05/03/2017.
 */
public class NewIdentityStatusMessageComposer extends MessageComposer {
    private final int status;

    public NewIdentityStatusMessageComposer(int status) {
        this.status = status;
    }

    @Override
    public short getId() {
        return Composers.NewIdentityStatusMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(status);
    }
}
