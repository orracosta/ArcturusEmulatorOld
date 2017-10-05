package com.habboproject.server.network.messages.incoming.room.pets.horse;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.pets.horse.HorseFigureMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class ApplyHorseEffectMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int itemId = msg.readInt();
        final int petId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();
        RoomItemFloor effectItem = room.getItems().getFloorItem(ItemManager.getInstance().getItemIdByVirtualId(itemId));

        if (effectItem == null) {
            return;
        }

        PetEntity petEntity = room.getEntities().getEntityByPetId(petId);

        if (petEntity == null || petEntity.getData().getOwnerId() != client.getPlayer().getId() || effectItem.getOwner() != client.getPlayer().getId()) {
            return;
        }

        if(effectItem.getDefinition().getItemName().contains("saddle")) {
            petEntity.getData().setSaddled(true);
        } else if(effectItem.getDefinition().getItemName().startsWith("horse_dye")) {
            int race = Integer.valueOf(effectItem.getDefinition().getItemName().split("_")[2]);
            int raceType = race * 4 - 2;
            if (race >= 13 && race <= 16) {
                raceType = (2 + race) * 4 + 1;
            }

            petEntity.getData().setRaceId(raceType);
        } else if(effectItem.getDefinition().getItemName().startsWith("horse_hairdye")) {
            petEntity.getData().setHairDye(48 + Integer.parseInt(effectItem.getDefinition().getItemName().split("_")[2]));
        } else if(effectItem.getDefinition().getItemName().startsWith("horse_hairstyle")) {
            petEntity.getData().setHair(100 + Integer.parseInt(effectItem.getDefinition().getItemName().split("_")[2]));
        }

        petEntity.getData().saveHorseData();
        petEntity.markNeedsUpdate();
        room.getEntities().broadcastMessage(new HorseFigureMessageComposer(petEntity));
        room.getItems().removeItem(effectItem, client, false, true);
    }
}
