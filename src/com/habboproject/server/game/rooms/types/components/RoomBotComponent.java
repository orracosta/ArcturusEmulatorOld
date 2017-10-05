package com.habboproject.server.game.rooms.types.components;

import com.habboproject.server.game.bots.BotData;
import com.habboproject.server.game.players.components.types.inventory.InventoryBot;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.data.PlayerBotData;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.storage.queries.bots.RoomBotDao;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;


public class RoomBotComponent {
    private Room room;

    private Map<String, Integer> botNameToId;

    public RoomBotComponent(Room room) {
        this.room = room;

        this.botNameToId = Maps.newHashMap();

        this.load();
    }

    public void dispose() {
        this.botNameToId.clear();
    }

    public void load() {
        try {
            List<BotData> botData = RoomBotDao.getBotsByRoomId(this.room.getId());

            for (BotData data : botData) {

                if (this.botNameToId.containsKey(data.getUsername())) {
                    data.setUsername(this.getAvailableName(data.getUsername()));
                }

                BotEntity botEntity = new BotEntity(data, room.getEntities().getFreeId(), ((PlayerBotData) data).getPosition(), 2, 2, room);
                this.botNameToId.put(botEntity.getUsername(), botEntity.getBotId());

                botEntity.getPosition().setZ(this.getRoom().getMapping().getStepHeight(botEntity.getPosition()));

                this.getRoom().getEntities().addEntity(botEntity);

                for (RoomItemFloor roomItemFloor : this.getRoom().getItems().getItemsOnSquare(((PlayerBotData) data).getPosition().getX(), ((PlayerBotData) data).getPosition().getY())) {
                    roomItemFloor.onEntityStepOn(botEntity);
                }
            }
        } catch (Exception e) {
            room.log.error("Error while deploying bots", e);
        }
    }

    public String getAvailableName(String name) {
        int usedCount = 0;

        for (String usedName : this.botNameToId.keySet()) {
            if (name.startsWith(usedName)) {
                usedCount++;
            }
        }

        if (usedCount == 0) return name;

        return name + usedCount;
    }

    public BotEntity addBot(InventoryBot bot, int x, int y, double height) {
        int virtualId = room.getEntities().getFreeId();
        String name;

        if (this.botNameToId.containsKey(bot.getName())) {
            name = this.getAvailableName(bot.getName());
        } else {
            name = bot.getName();
        }

        this.botNameToId.put(bot.getName(), bot.getId());

        BotData botData = new PlayerBotData(bot.getId(), name, bot.getMotto(), bot.getFigure(), bot.getGender(), bot.getOwnerName(), bot.getOwnerId(), "[]", true, 7, bot.getType(), bot.getMode(), null);
        BotEntity botEntity = new BotEntity(botData, virtualId, new Position(x, y, height), 1, 1, room);

        if (botEntity.getPosition().getZ() < this.getRoom().getModel().getSquareHeight()[x][y]) {
            botEntity.getPosition().setZ(this.getRoom().getModel().getSquareHeight()[x][y]);
        }

        this.getRoom().getEntities().addEntity(botEntity);
        return botEntity;
    }

    public BotEntity getBotByName(String name) {
        if (this.botNameToId.containsKey(name)) {
            return this.getRoom().getEntities().getEntityByBotId(this.botNameToId.get(name));
        }

        return null;
    }

    public Room getRoom() {
        return this.room;
    }

    public void changeBotName(String currentName, String newName) {
        if (!this.botNameToId.containsKey(currentName)) return;

        int botId = this.botNameToId.get(currentName);

        this.botNameToId.remove(currentName);
        this.botNameToId.put(newName, botId);
    }

    public void removeBot(String name) {
        this.botNameToId.remove(name);
    }
}
