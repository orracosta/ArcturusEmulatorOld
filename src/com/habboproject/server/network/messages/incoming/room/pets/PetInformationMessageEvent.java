package com.habboproject.server.network.messages.incoming.room.pets;

import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.pets.PetInformationMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class PetInformationMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getEntity() == null) return;

        int petId = msg.readInt();

        PetEntity petEntity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByPetId(petId);

        if (petEntity == null) {
            // its a player
            PlayerEntity playerEntity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByPlayerId(petId);

            if (playerEntity != null) {
                client.send(new PetInformationMessageComposer(playerEntity));
            }

            return;
        }

        client.send(new PetInformationMessageComposer(petEntity));
    }
}
