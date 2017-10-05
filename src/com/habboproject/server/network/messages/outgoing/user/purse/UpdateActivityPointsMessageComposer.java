package com.habboproject.server.network.messages.outgoing.user.purse;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class UpdateActivityPointsMessageComposer extends MessageComposer {

    private int activityPoints;
    private int change;

    public UpdateActivityPointsMessageComposer(int activityPoints, int change) {
        this.activityPoints = activityPoints;
        this.change = change;
    }

    @Override
    public short getId() {
        return Composers.HabboActivityPointNotificationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.activityPoints);
        msg.writeInt(this.change);
        msg.writeInt(0);
    }
}
