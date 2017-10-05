package com.habboproject.server.network.messages.outgoing.camera;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

/**
 * Created by brend on 03/03/2017.
 */
public class PhotoPriceMessageComposer extends MessageComposer {
    private final int costCredits;
    private final int costDuckets;

    public PhotoPriceMessageComposer(int costCredits, int costDuckets) {
        this.costCredits = costCredits;
        this.costDuckets = costDuckets;
    }

    @Override
    public short getId() {
        return Composers.PhotoPriceMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(costCredits);
        msg.writeInt(costDuckets);
        msg.writeInt(0);
    }
}
