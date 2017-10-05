package com.habboproject.server.network.messages.outgoing.catalog;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.game.catalog.types.CatalogItem;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class BoughtItemMessageComposer extends MessageComposer {
    private final CatalogItem catalogItem;
    private final ItemDefinition itemDefinition;

    private final boolean isGroup;

    private BoughtItemMessageComposer(final CatalogItem catalogItem, final ItemDefinition itemDefinition, final boolean isGroup) {
        this.catalogItem = catalogItem;
        this.itemDefinition = itemDefinition;
        this.isGroup = isGroup;
    }

    public BoughtItemMessageComposer(final CatalogItem catalogItem, final ItemDefinition itemDefinition) {
        this(catalogItem, itemDefinition, false);
    }

    public BoughtItemMessageComposer(final PurchaseType type) {
        this(null, null, type == PurchaseType.GROUP);
    }

    public enum PurchaseType {
        GROUP,
        BADGE
    }

    @Override
    public short getId() {
        return Composers.PurchaseOKMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        if (this.catalogItem != null && this.itemDefinition != null) {
            msg.writeInt(this.catalogItem.getId());
            msg.writeString(this.itemDefinition.getItemName());
            msg.writeBoolean(false);
            msg.writeInt(this.catalogItem.getCostCredits());
            msg.writeInt(this.catalogItem.getCostActivityPoints());
            msg.writeInt(0);
            msg.writeBoolean(false);
            msg.writeInt(1);
            msg.writeString(this.itemDefinition.getType());
            msg.writeInt(this.itemDefinition.getSpriteId());
            msg.writeString("");
            msg.writeInt(1);
            msg.writeString("");
            msg.writeInt(1);

            return;
        }

        if (this.isGroup) {
            msg.writeInt(6165);
            msg.writeString("CREATE_GUILD");
            msg.writeBoolean(false);
            msg.writeInt(CometSettings.groupCost);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeBoolean(true);
            msg.writeInt(0);
            msg.writeInt(2);
            msg.writeBoolean(false);
        } else {
            msg.writeInt(0);
            msg.writeString("");
            msg.writeBoolean(false);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeBoolean(true);
            msg.writeInt(1);
            msg.writeString("s");
            msg.writeInt(0);
            msg.writeString("");
            msg.writeInt(1);
            msg.writeInt(0);
            msg.writeString("");
            msg.writeInt(1);
        }
    }
}
