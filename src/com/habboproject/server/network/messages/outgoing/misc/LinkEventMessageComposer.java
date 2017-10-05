package com.habboproject.server.network.messages.outgoing.misc;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

/**
 * Created by brend on 03/03/2017.
 */
public class LinkEventMessageComposer extends MessageComposer {
    private final String link;

    public LinkEventMessageComposer(String link) {
        this.link = link;
    }

    @Override
    public short getId() {
        return Composers.LinkEventMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(link);
    }
}
