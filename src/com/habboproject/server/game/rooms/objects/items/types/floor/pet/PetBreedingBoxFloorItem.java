package com.habboproject.server.game.rooms.objects.items.types.floor.pet;

import com.google.common.collect.Lists;
import com.habboproject.server.game.pets.races.BreedingType;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;

import java.util.List;

/**
 * Created by brend on 18/03/2017.
 */
public class PetBreedingBoxFloorItem extends RoomItemFloor {
    private List<PetEntity> entities;
    private BreedingType type;

    public PetBreedingBoxFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        this.entities = Lists.newArrayList();

        switch (this.getDefinition().getInteraction()) {
            case "breeding_dog":
                type = BreedingType.DOG;
                break;

            case "breeding_cat":
                type = BreedingType.CAT;
                break;

            case "breeding_terrier":
                type = BreedingType.TERRIER;
                break;

            case "breeding_bear":
                type = BreedingType.BEAR;
                break;

            case "breeding_pig":
                type = BreedingType.PIG;
                break;
        }
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if(entity == null || !(entity instanceof PetEntity)) {
            return;
        }

        if (!this.getEntities().contains(entity)) {
            this.getEntities().add(((PetEntity)entity));

            ((PetEntity) entity).getPetAI().possibleBreeding();
        }

        if (this.getEntities().size() == 2) {
            Session owner = NetworkManager.getInstance().getSessions().getByPlayerId(((PetEntity) entity).getData().getOwnerId());
            if (owner != null) {
                owner.send(new MotdNotificationMessageComposer("Okay!"));
            }
        }
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if(entity == null || !(entity instanceof PetEntity)) {
            return;
        }

        if (this.getEntities().contains(entity)) {
            this.getEntities().remove(entity);
        }

        entity.setOverriden(false);
    }

    @Override
    public boolean isMovementCancelled(RoomEntity entity) {
        if (entity == null)
            return true;

        if (!(entity instanceof PetEntity))
            return true;

        if (this.getEntities().size() == 2 && !this.getEntities().contains(entity))
            return true;

        return false;
    }

    public List<PetEntity> getEntities() {
        return entities;
    }

    public BreedingType getType() {
        return type;
    }
}
