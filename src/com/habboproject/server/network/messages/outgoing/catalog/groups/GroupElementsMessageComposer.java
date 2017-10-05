package com.habboproject.server.network.messages.outgoing.catalog.groups;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.items.types.*;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class GroupElementsMessageComposer extends MessageComposer {
    public GroupElementsMessageComposer() {

    }

    @Override
    public short getId() {
        return Composers.BadgeEditorPartsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(GroupManager.getInstance().getGroupItems().getBases().size());

        for (GroupBase base : GroupManager.getInstance().getGroupItems().getBases()) {
            msg.writeInt(base.getId());
            msg.writeString(base.getValueA());
            msg.writeString(base.getValueB());
        }

        msg.writeInt(GroupManager.getInstance().getGroupItems().getSymbols().size());

        for (GroupSymbol symbol : GroupManager.getInstance().getGroupItems().getSymbols()) {
            msg.writeInt(symbol.getId());
            msg.writeString(symbol.getValueA());
            msg.writeString(symbol.getValueB());
        }

        msg.writeInt(GroupManager.getInstance().getGroupItems().getBaseColours().size());

        for (GroupBaseColour colour : GroupManager.getInstance().getGroupItems().getBaseColours()) {
            msg.writeInt(colour.getId());
            msg.writeString(colour.getColour());
        }

        msg.writeInt(GroupManager.getInstance().getGroupItems().getSymbolColours().size());

        for (GroupSymbolColour colour : GroupManager.getInstance().getGroupItems().getSymbolColours().values()) {
            msg.writeInt(colour.getId());
            msg.writeString(colour.getColour());
        }

        msg.writeInt(GroupManager.getInstance().getGroupItems().getBackgroundColours().size());

        for (GroupBackgroundColour colour : GroupManager.getInstance().getGroupItems().getBackgroundColours().values()) {
            msg.writeInt(colour.getId());
            msg.writeString(colour.getColour());
        }
    }
}
