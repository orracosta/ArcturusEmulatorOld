package com.habboproject.server.network.messages.outgoing.navigator;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.navigator.types.Category;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;

public class RoomCategoriesMessageComposer extends MessageComposer {
    private final List<Category> categories;
    private final int rank;

    public RoomCategoriesMessageComposer(final List<Category> categories, final int rank) {
        this.categories = categories;
        this.rank = rank;
    }

    @Override
    public short getId() {
        return Composers.UserFlatCatsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.categories.size());

        for (Category cat : this.categories) {
            msg.writeInt(cat.getId());
            msg.writeString(cat.getPublicName());
            msg.writeBoolean(cat.getRequiredRank() <= this.rank);
            msg.writeBoolean(false);
            msg.writeString("");
            msg.writeString("");
            msg.writeBoolean(false);
        }
    }
}
