package com.habboproject.server.network.messages.incoming.room.bots;

import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.players.components.types.inventory.InventoryBot;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.game.rooms.types.tiles.RoomTileState;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.BotInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.bots.RoomBotDao;


public class PlaceBotMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int botId = msg.readInt();
        int x = msg.readInt();
        int y = msg.readInt();

        Room room = client.getPlayer().getEntity().getRoom();
        InventoryBot bot = (InventoryBot) client.getPlayer().getBots().getBot(botId);

        if (room == null || bot == null || (!room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        if (room.getEntities().getBotEntities().size() >= CometSettings.roomMaxBots) {
            client.send(new AlertMessageComposer(String.format(Locale.getOrDefault("comet.game.bots.toomany", "You can only have %s bots per room!"), CometSettings.roomMaxBots)));
            return;
        }

        double height = room.getMapping().getTile(x, y).getWalkHeight();
        final Position position = new Position(x, y, height);

        final RoomTile tile = room.getMapping().getTile(x, y);

        if (tile == null || !room.getMapping().isValidPosition(position) || room.getModel().getSquareState()[x][y] != RoomTileState.VALID) {
            return;
        }

        if (tile.getEntities().size() >= 1) {
            return;
        }

        RoomBotDao.savePosition(x, y, height, botId, room.getId());

        BotEntity botEntity = room.getBots().addBot(bot, x, y, height);
        client.getPlayer().getBots().remove(botId);

        tile.getEntities().add(botEntity);

        room.getEntities().broadcastMessage(new AvatarsMessageComposer(botEntity));
        client.send(new BotInventoryMessageComposer(client.getPlayer().getBots().getBots()));

        for (RoomItemFloor floorItem : room.getItems().getItemsOnSquare(x, y)) {
            floorItem.onEntityStepOn(botEntity);
        }

        botEntity.getAI().onAddedToRoom();
    }
}
