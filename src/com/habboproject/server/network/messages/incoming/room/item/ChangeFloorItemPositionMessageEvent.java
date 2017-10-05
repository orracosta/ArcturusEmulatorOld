package com.habboproject.server.network.messages.incoming.room.item;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.Map;


public class ChangeFloorItemPositionMessageEvent implements Event {
    private static Logger log = Logger.getLogger(ChangeFloorItemPositionMessageEvent.class);

    public void handle(Session client, MessageEvent msg) {
        Long id = ItemManager.getInstance().getItemIdByVirtualId(msg.readInt());

        if(id == null) {
            return;
        }

        int x = msg.readInt();
        int y = msg.readInt();
        int rot = msg.readInt();

        if (client.getPlayer().getEntity() == null) return;

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) return;

        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId())
                && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        RoomItemFloor floorItem = room.getItems().getFloorItem(id);

        if (floorItem != null) {
            if (rot != floorItem.getRotation()) {
                client.getPlayer().getQuests().progressQuest(QuestType.FURNI_ROTATE);
            }

            client.getPlayer().getQuests().progressQuest(QuestType.FURNI_MOVE);
        }

        try {
            if (room.getItems().moveFloorItem(id, new Position(x, y), rot, true)) {
                if (floorItem != null && floorItem.getTile().getItems().size() > 1) {
                    client.getPlayer().getQuests().progressQuest(QuestType.FURNI_STACK);
                }
            } else {
                Map<String, String> notificationParams = Maps.newHashMap();

                notificationParams.put("message", "${room.error.cant_set_item}");

                client.send(new NotificationMessageComposer("furni_placement_error", notificationParams));
            }

            if (floorItem != null) {
                room.getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(floorItem));
            }
        } catch (Exception e) {
            log.error("Error whilst changing floor item position!", e);
        }
    }
}