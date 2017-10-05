package com.habboproject.server.network.messages.incoming.room.pets.horse;

import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.utilities.DistanceCalculator;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.pets.horse.HorseFigureMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class RideHorseMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int entityId = msg.readInt();

        if (client.getPlayer().getEntity() == null) {
            return;
        }

        if (client.getPlayer().getEntity().getMountedEntity() != null) {
            client.getPlayer().getEntity().getMountedEntity().setHasMount(false);

            client.getPlayer().getEntity().moveTo(client.getPlayer().getEntity().getMountedEntity().getPosition().squareInFront(0));

            client.getPlayer().getEntity().getMountedEntity().setMountedEntity(null);
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new HorseFigureMessageComposer(((PetEntity) client.getPlayer().getEntity().getMountedEntity())));

            client.getPlayer().getEntity().setMountedEntity(null);
            client.getPlayer().getEntity().setHasMount(false);
            client.getPlayer().getEntity().applyEffect(null);

            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) return;

        PetEntity horse = room.getEntities().getEntityByPetId(entityId);

        if (horse == null) {
            // its a user.
            if (entityId == client.getPlayer().getId()) return;

            PlayerEntity playerEntity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByPlayerId(entityId);

            if (playerEntity != null) {
                if (!playerEntity.getMotto().toLowerCase().startsWith("rideable")) {
                    return;
                }

                client.getPlayer().getEntity().setMountedEntity(playerEntity);
                client.getPlayer().getEntity().applyEffect(new PlayerEffect(103, 0));
                client.getPlayer().getEntity().warp(playerEntity.getPosition());

                playerEntity.setOverriden(true);
                playerEntity.setHasMount(true);
            }
            return;
        }

        if (!DistanceCalculator.tilesTouching(client.getPlayer().getEntity().getPosition(), horse.getPosition())) {
            Position closePosition = horse.getPosition().squareBehind(6);

            client.getPlayer().getEntity().moveTo(closePosition.getX(), closePosition.getY());

            horse.getPetAI().waitForInteraction();
            return;
        }

        if(horse.getData().getOwnerId() != client.getPlayer().getId() && !horse.getData().isAnyRider()) {
            return;
        }

        if(horse.getMountedEntity() != null) {
            return;
        }

        if (horse.isWalking()) {
            horse.cancelWalk();
        }

        client.getPlayer().getEntity().applyEffect(new PlayerEffect(103, 0));

        client.getPlayer().getEntity().cancelWalk();
        client.getPlayer().getEntity().setBodyRotation(horse.getBodyRotation());
        client.getPlayer().getEntity().setHeadRotation(horse.getHeadRotation());

        Position warpPosition = horse.getPosition().copy();

        warpPosition.setZ(warpPosition.getZ() + 1.0);

        client.getPlayer().getEntity().warpImmediately(warpPosition);
        client.getPlayer().getEntity().setMountedEntity(horse);
        client.getPlayer().getEntity().setIsMoonwalking(false);

        horse.setMountedEntity(client.getPlayer().getEntity());
        horse.setHasMount(true);

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new HorseFigureMessageComposer(horse));
    }
}
