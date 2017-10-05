package com.habboproject.server.network.messages.incoming.room.item;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.google.common.collect.Maps;

import java.util.Map;


public class PlaceItemMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer() == null || client.getPlayer().getEntity() == null) {
            return;
        }

        String data = msg.readString();

        if(client.getPlayer() == null || client.getPlayer().getInventory() == null) return;

        if (data == null) return;

        String[] parts = data.split(" ");
        int id = Integer.parseInt(parts[0].replace("-", ""));

        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId())
                && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            Map<String, String> notificationParams = Maps.newHashMap();

            notificationParams.put("message", "${room.error.cant_set_not_owner}");

            client.send(new NotificationMessageComposer("furni_placement_error", notificationParams));
            return;
        }

        try {
            if (parts.length > 1 && parts[1].startsWith(":")) {
                String position = Position.validateWallPosition(parts[1] + " " + parts[2] + " " + parts[3]);

                if (position == null) {
                    return;
                }

                Long itemId = ItemManager.getInstance().getItemIdByVirtualId(id);

                if(itemId == null) {
                    return;
                }

                PlayerItem item = client.getPlayer().getInventory().getWallItem(itemId);

                if (item == null) {
                    return;
                }

                client.getPlayer().getEntity().getRoom().getItems().placeWallItem(item, position, client.getPlayer());
            } else {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int rot = Integer.parseInt(parts[3]);

                Long itemId = ItemManager.getInstance().getItemIdByVirtualId(id);

                if(itemId == null) {
                    return;
                }

                PlayerItem item = client.getPlayer().getInventory().getFloorItem(itemId);

                if (item == null) {
                    return;
                }

                client.getPlayer().getEntity().getRoom().getItems().placeFloorItem(item, x, y, rot, client.getPlayer());

                if(client.getPlayer().getEntity().getRoom().getItems() == null) {
                    return;
                }

                RoomItemFloor floorItem = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(item.getId());

                if (floorItem != null) {
                    RoomTile tile = floorItem.getTile();

                    if (tile != null) {
                        if (tile.getItems().size() > 1) {
                            client.getPlayer().getQuests().progressQuest(QuestType.FURNI_STACK);
                        }
                    }
                }
            }
        } catch (Exception e) {
            client.getLogger().error("Error while placing item", e);
        }

        client.getPlayer().getQuests().progressQuest(QuestType.FURNI_PLACE);
    }
}
