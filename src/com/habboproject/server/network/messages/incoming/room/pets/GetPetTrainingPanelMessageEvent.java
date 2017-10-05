package com.habboproject.server.network.messages.incoming.room.pets;

import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.pets.PetTrainingPanelMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class GetPetTrainingPanelMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int petId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) return;

        Room room = client.getPlayer().getEntity().getRoom();
        PetEntity petEntity = room.getEntities().getEntityByPetId(petId);
        PlayerEntity playerEntity = client.getPlayer().getEntity();

        if (petEntity == null) return;

        client.send(new PetTrainingPanelMessageComposer(petEntity.getData()));
    }
}
