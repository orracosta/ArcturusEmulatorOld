package com.habboproject.server.network.messages.outgoing.user.inventory;

import com.habboproject.server.api.game.players.data.components.PlayerInventory;
import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.Map;


public class InventoryMessageComposer extends MessageComposer {
    private final PlayerInventory inventoryComponent;

    public InventoryMessageComposer(final PlayerInventory inventoryComponent) {
        this.inventoryComponent = inventoryComponent;
    }

    @Override
    public short getId() {
        return Composers.FurniListMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(1);
        msg.writeInt(1);
        msg.writeInt(inventoryComponent.getTotalSize());

        for (Map.Entry<Long, PlayerItem>  inventoryItem : inventoryComponent.getFloorItems().entrySet()) {
            inventoryItem.getValue().compose(msg);
        }

        // Wall items
        for (Map.Entry<Long, PlayerItem> item : inventoryComponent.getWallItems().entrySet()) {
            msg.writeInt(item.getValue().getVirtualId());
            msg.writeString(item.getValue().getDefinition().getType().toUpperCase());
            msg.writeInt(item.getValue().getVirtualId());
            msg.writeInt(item.getValue().getDefinition().getSpriteId());

            if (item.getValue().getDefinition().getItemName().contains("a2")) {
                msg.writeInt(3);
            } else if (item.getValue().getDefinition().getItemName().contains("wallpaper")) {
                msg.writeInt(2);
            } else if (item.getValue().getDefinition().getItemName().contains("landscape")) {
                msg.writeInt(4);
            } else {
                msg.writeInt(1);
            }

            msg.writeInt(0);
            msg.writeString(item.getValue().getExtraData());

            msg.writeBoolean(item.getValue().getDefinition().canRecycle());
            msg.writeBoolean(item.getValue().getDefinition().canTrade());
            msg.writeBoolean(item.getValue().getDefinition().canInventoryStack());
            msg.writeBoolean(item.getValue().getDefinition().canMarket());
            msg.writeInt(-1);
            msg.writeBoolean(false);
            msg.writeInt(-1);
        }
    }
}
