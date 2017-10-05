package com.habboproject.server.network.messages.outgoing.camera;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

/**
 * Created by brend on 01/03/2017.
 */
public class PhotoPurchaseMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.PhotoPurchaseMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {

    }
}
