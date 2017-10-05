package com.habboproject.server.network.messages.outgoing.room.items;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.Map;

public class SlideObjectBundleMessageComposer extends MessageComposer {
    private final Position from;
    private final Position to;
    private final int rollerItemId;
    private final int avatarId;
    private final int itemId;
    private final Map<Integer, Double> items;

    public SlideObjectBundleMessageComposer(Position from, Position to, int rollerItemId, int avatarId, int itemId) {
        this.from = from;
        this.to = to;
        this.rollerItemId = rollerItemId;
        this.avatarId = avatarId;
        this.itemId = itemId;
        this.items = null;
    }

    public SlideObjectBundleMessageComposer(Position from, Position to, int rollerItemId, int avatarId, Map<Integer, Double> items) {
        this.from = from;
        this.to = to;
        this.rollerItemId = rollerItemId;
        this.avatarId = avatarId;
        this.itemId = 0;
        this.items = items;
    }

    @Override
    public short getId() {
        return Composers.SlideObjectBundleMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        if(this.items == null) {
            composeLegacy(msg);
            return;
        }

        msg.writeInt(from.getX());
        msg.writeInt(from.getY());

        msg.writeInt(to.getX());
        msg.writeInt(to.getY());

        msg.writeInt(this.items.size());

        for(Map.Entry<Integer, Double> item : this.items.entrySet()) {
            msg.writeInt(item.getKey());

            // we want to ensure we slide to the same height as we were previously at.
            msg.writeDouble(item.getValue());
            msg.writeDouble(item.getValue());
        }

        msg.writeInt(this.rollerItemId);

        if(this.avatarId != 0) {
            // 1 = mv, 2 = std
            msg.writeInt(2);

            msg.writeInt(this.avatarId);
            msg.writeDouble(from.getZ());
            msg.writeDouble(to.getZ());
        }
    }

    private void composeLegacy(IComposer msg) {
        final boolean isItem = itemId > 0;

        msg.writeInt(from.getX());
        msg.writeInt(from.getY());

        msg.writeInt(to.getX());
        msg.writeInt(to.getY());

        msg.writeInt(isItem ? 1 : 0);

        if (isItem) {
            msg.writeInt(itemId);
        } else {
            msg.writeInt(rollerItemId);
            msg.writeInt(2);
            msg.writeInt(avatarId);
        }

        msg.writeDouble(from.getZ());
        msg.writeDouble(to.getZ());

        if (isItem) {
            msg.writeInt(rollerItemId);
        }
    }
}
