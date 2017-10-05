package com.habboproject.server.network.messages.outgoing.user.wardrobe;

import com.habboproject.server.api.game.players.data.types.IWardrobeItem;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;


public class WardrobeMessageComposer extends MessageComposer {
    private final List<IWardrobeItem> wardrobe;

    public WardrobeMessageComposer(final List<IWardrobeItem> wardrobe) {
        this.wardrobe = wardrobe;
    }

    @Override
    public short getId() {
        return Composers.WardrobeMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(1);
        msg.writeInt(wardrobe.size());

        for (IWardrobeItem item : wardrobe) {
            msg.writeInt(item.getSlot());
            msg.writeString(item.getFigure());

            if (item.getGender() != null) {
                msg.writeString(item.getGender().toUpperCase());
            } else {
                msg.writeString("M");
            }
        }
    }
}
