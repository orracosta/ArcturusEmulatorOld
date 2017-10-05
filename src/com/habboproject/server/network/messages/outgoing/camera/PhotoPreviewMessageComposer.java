package com.habboproject.server.network.messages.outgoing.camera;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

/**
 * Created by brend on 03/03/2017.
 */
public class PhotoPreviewMessageComposer extends MessageComposer {
    private final String fileUrl;

    public PhotoPreviewMessageComposer(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public short getId() {
        return Composers.PhotoPreviewMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(fileUrl);
    }
}
