package com.habboproject.server.network.messages.incoming.room.bots;

import com.habboproject.server.game.players.components.types.inventory.InventoryBot;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.inventory.BotInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.bots.RoomBotDao;


public class RemoveBotMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        BotEntity entity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByBotId(msg.readInt());

        if (entity == null) {
            return;
        }

        if (client.getPlayer().getId() != entity.getData().getOwnerId() && !client.getPlayer().getPermissions().getRank().roomFullControl() && !client.getPlayer().getPermissions().getRank().roomFullAcessPublic()) {
            return;
        }

        if (entity.getBotId() > 0) {
            InventoryBot bot = new InventoryBot(entity.getBotId(), entity.getData().getOwnerId(), entity.getData().getOwnerName(), entity.getUsername(), entity.getFigure(), entity.getGender(), entity.getMotto(), entity.getData().getBotType());

            client.getPlayer().getBots().addBot(bot);

            RoomBotDao.setRoomId(0, entity.getBotId());
            client.send(new BotInventoryMessageComposer(client.getPlayer().getBots().getBots()));
        }

        entity.leaveRoom();
    }
}
