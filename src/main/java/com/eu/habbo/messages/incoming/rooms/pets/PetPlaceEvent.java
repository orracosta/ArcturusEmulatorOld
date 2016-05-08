package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.PetErrorComposer;
import com.eu.habbo.messages.outgoing.inventory.RemovePetComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;

/**
 * Created on 30-11-2014 10:46.
 */
public class PetPlaceEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        if(this.client.getHabbo().getHabboInfo().getId() != room.getOwnerId() && !room.isAllowPets() && !(this.client.getHabbo().hasPermission("acc_anyroomowner") || this.client.getHabbo().hasPermission("acc_placefurni")))
        {
            this.client.sendResponse(new PetErrorComposer(PetErrorComposer.ROOM_ERROR_PETS_FORBIDDEN_IN_FLAT));
            return;
        }

        int petId = this.packet.readInt();

        Pet pet = this.client.getHabbo().getHabboInventory().getPetsComponent().getPet(petId);

        if(pet == null)
        {
            return;
        }

        int x = this.packet.readInt();
        int y = this.packet.readInt();

        if(room.getCurrentPets().size() >= Emulator.getConfig().getInt("hotel.pets.max.room") && !this.client.getHabbo().hasPermission("acc_unlimited_pets"))
        {
            this.client.sendResponse(new PetErrorComposer(PetErrorComposer.ROOM_ERROR_MAX_PETS));
            return;
        }

        HabboItem item = room.getTopItemAt(x, y);
        if(item != null && !item.getBaseItem().allowStack())
        {
            this.client.sendResponse(new PetErrorComposer(PetErrorComposer.ROOM_ERROR_PETS_SELECTED_TILE_NOT_FREE));
            return;
        }

        pet.setRoom(room);
        RoomUnit roomUnit = pet.getRoomUnit();

        if(roomUnit == null)
        {
            roomUnit = new RoomUnit();
        }

        roomUnit.setRotation(RoomUserRotation.SOUTH);
        roomUnit.setX(x);
        roomUnit.setY(y);
        roomUnit.setZ(room.getStackHeight(x, y, false));
        roomUnit.setGoalLocation(x, y);
        roomUnit.setPathFinderRoom(room);
        roomUnit.setRoomUnitType(RoomUnitType.PET);
        pet.setRoomUnit(roomUnit);
        room.addPet(pet);
        roomUnit.setId(room.getUnitCounter());
        pet.needsUpdate = true;
        Emulator.getThreading().run(pet);
        room.sendComposer(new RoomPetComposer(pet).compose());
        this.client.getHabbo().getHabboInventory().getPetsComponent().removePet(pet);
        this.client.sendResponse(new RemovePetComposer(pet));
    }
}
