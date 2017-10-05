package com.habboproject.server.network.messages.outgoing.room.items.wired.dialog;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredUtil;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredTriggerItem;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;

public class WiredActionMessageComposer extends MessageComposer {
    private final List<WiredTriggerItem> incompatibleTriggers;
    private final WiredActionItem wiredAction;

    public WiredActionMessageComposer(final WiredActionItem wiredAction) {
        this.wiredAction = wiredAction;
        this.incompatibleTriggers = wiredAction.getIncompatibleTriggers();
    }

    @Override
    public short getId() {
        return Composers.WiredEffectConfigMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeBoolean(false); // advanced
        msg.writeInt(WiredUtil.MAX_FURNI_SELECTION);

        msg.writeInt(wiredAction.getWiredData().getSelectedIds().size());

        for (Long itemId : wiredAction.getWiredData().getSelectedIds()) {
            msg.writeInt(ItemManager.getInstance().getItemVirtualId(itemId));
        }

        msg.writeInt(wiredAction.getDefinition().getSpriteId());
        msg.writeInt(wiredAction.getVirtualId());

        msg.writeString(wiredAction.getWiredData().getText());

        msg.writeInt(wiredAction.getWiredData().getParams().size());

        for (int param : wiredAction.getWiredData().getParams().values()) {
            msg.writeInt(param);
        }

        msg.writeInt(wiredAction.getWiredData().getSelectionType());
        msg.writeInt(wiredAction.getInterface());
        msg.writeInt(wiredAction.getWiredData().getDelay());

        msg.writeInt(incompatibleTriggers.size());

        for (WiredTriggerItem incompatibleTrigger : incompatibleTriggers) {
            msg.writeInt(incompatibleTrigger.getDefinition().getSpriteId());
        }

//        msg.writeString(""); //no idea
    }

    @Override
    public void dispose() {
        this.incompatibleTriggers.clear();
    }
}
