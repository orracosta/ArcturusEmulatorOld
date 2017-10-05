package com.habboproject.server.game.rooms.types.components;

import com.habboproject.server.game.pets.data.PetData;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.storage.queries.pets.RoomPetDao;


public class PetComponent {
    private Room room;

    public PetComponent(Room room) {
        this.room = room;

        this.load();
    }

    public void load() {
        for (PetData data : RoomPetDao.getPetsByRoomId(this.room.getId())) {
            PetEntity petEntity = new PetEntity(data, room.getEntities().getFreeId(), data.getRoomPosition(), 3, 3, room);

            this.getRoom().getEntities().addEntity(petEntity);
        }
    }

    public PetEntity addPet(PetData pet, Position position) {
        RoomPetDao.updatePet(this.room.getId(), position.getX(), position.getY(), pet.getId());

        int virtualId = room.getEntities().getFreeId();
        PetEntity petEntity = new PetEntity(pet, virtualId, position, 3, 3, room);
        this.getRoom().getEntities().addEntity(petEntity);

        return petEntity;
    }

    public Room getRoom() {
        return this.room;
    }
}
