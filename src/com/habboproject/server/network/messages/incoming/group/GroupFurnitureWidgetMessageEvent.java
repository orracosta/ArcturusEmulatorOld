package com.habboproject.server.network.messages.incoming.group;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.groups.GroupFloorItem;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.GroupFurnitureWidgetMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GroupFurnitureWidgetMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int itemId = msg.readInt();

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            RoomItemFloor floorItem = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(itemId);

            if (floorItem != null && floorItem instanceof GroupFloorItem) {
                Group group = GroupManager.getInstance().get(((GroupFloorItem) floorItem).getGroupId());

                if (group != null) {
                    client.send(new GroupFurnitureWidgetMessageComposer(itemId, group.getId(), group.getData().getTitle(), group.getData().getRoomId(), client.getPlayer().getGroups().contains(group.getId()), false));
                }
            }
        }
    }
}
