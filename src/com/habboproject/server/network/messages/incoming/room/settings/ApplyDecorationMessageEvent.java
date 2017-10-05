package com.habboproject.server.network.messages.incoming.room.settings;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomPropertyMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import org.apache.log4j.Logger;

import java.util.Map;


public class ApplyDecorationMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        long itemId = ItemManager.getInstance().getItemIdByVirtualId(msg.readInt());

        PlayerItem item = client.getPlayer().getInventory().getWallItem(itemId);

        if (item == null) {
            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();

        boolean isOwner = client.getPlayer().getId() == room.getData().getOwnerId();
        boolean hasRights = room.getRights().hasRights(client.getPlayer().getId());

        if (isOwner || hasRights) {
            String type = "floor";
            Map<String, String> decorations = room.getData().getDecorations();
            String data = item.getExtraData();

            if (item.getDefinition().getItemName().contains("wallpaper")) {
                type = "wallpaper";
            } else if (item.getDefinition().getItemName().contains("landscape")) {
                type = "landscape";
            }

            if (decorations.containsKey(type)) {
                decorations.replace(type, data);
            } else {
                decorations.put(type, data);
            }

            if (type.equals("floor")) {
                client.getPlayer().getQuests().progressQuest(QuestType.FURNI_DECORATION_FLOOR);
            } else if (type.equals("wallpaper")) {
                client.getPlayer().getQuests().progressQuest(QuestType.FURNI_DECORATION_WALL);
            }

            client.getPlayer().getInventory().removeItem(item);
            RoomItemDao.deleteItem(itemId);
            client.send(new UpdateInventoryMessageComposer());

            try {
                room.getData().save();
                room.getEntities().broadcastMessage(new RoomPropertyMessageComposer(type, data));
            } catch (Exception e) {
                Logger.getLogger(ApplyDecorationMessageEvent.class.getName()).error("Error while saving room data", e);
            }
        }
    }
}
