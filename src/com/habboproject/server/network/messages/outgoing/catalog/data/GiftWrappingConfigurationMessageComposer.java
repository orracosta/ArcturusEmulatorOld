package com.habboproject.server.network.messages.outgoing.catalog.data;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class GiftWrappingConfigurationMessageComposer extends MessageComposer {
    private static final int[] giftColours = {
            0, 1, 2, 3, 4, 5, 6, 8
    };

    private static final int[] giftDecorations = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    };

    public GiftWrappingConfigurationMessageComposer() {

    }

    @Override
    public short getId() {
        return Composers.GiftWrappingConfigurationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(true);//?
        msg.writeInt(1);//?
        msg.writeInt(CatalogManager.getInstance().getGiftBoxesNew().size());

        for (int spriteId : CatalogManager.getInstance().getGiftBoxesNew()) {
            msg.writeInt(spriteId);
        }

        msg.writeInt(giftColours.length);

        for (int giftColour : giftColours) {
            msg.writeInt(giftColour);
        }

        msg.writeInt(giftDecorations.length);

        for (int giftDecoration : giftDecorations) {
            msg.writeInt(giftDecoration);
        }

        msg.writeInt(CatalogManager.getInstance().getGiftBoxesOld().size());

        for (int spriteId : CatalogManager.getInstance().getGiftBoxesOld()) {
            msg.writeInt(spriteId);
        }
    }
}
