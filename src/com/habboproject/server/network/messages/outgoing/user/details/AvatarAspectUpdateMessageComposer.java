package com.habboproject.server.network.messages.outgoing.user.details;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class AvatarAspectUpdateMessageComposer extends MessageComposer {

    private final String figure;
    private final String gender;

    public AvatarAspectUpdateMessageComposer(String figure, String gender) {
        this.figure = figure;
        this.gender = gender;
    }

    @Override
    public short getId() {
        return Composers.AvatarAspectUpdateMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.figure);
        msg.writeString(this.gender);
    }
}
