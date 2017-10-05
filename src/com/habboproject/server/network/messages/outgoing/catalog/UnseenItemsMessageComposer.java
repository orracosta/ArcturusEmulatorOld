package com.habboproject.server.network.messages.outgoing.catalog;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.google.common.collect.Maps;

import java.util.*;

public class UnseenItemsMessageComposer extends MessageComposer {
    private final Map<Integer, List<Integer>> newObjects;

    public UnseenItemsMessageComposer(Map<Integer, List<Integer>> newObjects) {
        this.newObjects = newObjects;
    }

    public UnseenItemsMessageComposer(Set<PlayerItem> PlayerItems) {
        this.newObjects = new HashMap<Integer, List<Integer>>();
        for (PlayerItem playerItem : PlayerItems) {
            if (!this.newObjects.containsKey(1)) {
                this.newObjects.put(1, new ArrayList<Integer>() {{
                    add(ItemManager.getInstance().getItemVirtualId(playerItem.getId()));
                }});

                continue;
            }
            this.newObjects.get(1).add(ItemManager.getInstance().getItemVirtualId(playerItem.getId()));
        }
    }

    public UnseenItemsMessageComposer(PlayerItem playerItem) {
        this.newObjects = Maps.newHashMap();
        this.newObjects.put(1, new ArrayList<Integer>() {{
            add(ItemManager.getInstance().getItemVirtualId(playerItem.getId()));
        }});
    }

    public short getId() {
        return 519;
    }

    public void compose(IComposer msg) {
        msg.writeInt(this.newObjects.size());
        for (Map.Entry<Integer, List<Integer>> tab : this.newObjects.entrySet()) {
            msg.writeInt(tab.getKey());
            msg.writeInt(tab.getValue().size());
            for (Integer item : tab.getValue()) {
                msg.writeInt(item);
            }
        }
    }

    public void dispose() {
        for (Map.Entry<Integer, List<Integer>> tab : this.newObjects.entrySet()) {
            tab.getValue().clear();
        }
        this.newObjects.clear();
    }
}
