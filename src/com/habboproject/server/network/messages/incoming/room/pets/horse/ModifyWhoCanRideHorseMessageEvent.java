package com.habboproject.server.network.messages.incoming.room.pets.horse;

import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.pets.horse.HorseFigureMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class ModifyWhoCanRideHorseMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int petId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        PetEntity petEntity = room.getEntities().getEntityByPetId(petId);

        if (petEntity == null || petEntity.getData().getOwnerId() != client.getPlayer().getId()) {
            return;
        }

        petEntity.getData().setAnyRider(!petEntity.getData().isAnyRider());

        petEntity.getData().saveHorseData();
        petEntity.markNeedsUpdate();
        room.getEntities().broadcastMessage(new HorseFigureMessageComposer(petEntity));
    }
}
