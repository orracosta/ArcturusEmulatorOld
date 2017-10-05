package com.habboproject.server.network.messages.incoming.camera;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.camera.PhotoPriceMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 03/03/2017.
 */
public class PhotoPriceMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new PhotoPriceMessageComposer(Comet.getServer().getConfig().getInt("comet.camera.price.credits"),
                Comet.getServer().getConfig().getInt("comet.camera.price.duckets")));
    }
}
