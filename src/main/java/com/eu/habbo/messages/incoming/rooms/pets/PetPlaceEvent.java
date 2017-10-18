package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.PetErrorComposer;
import com.eu.habbo.messages.outgoing.inventory.RemovePetComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;

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

        Pet pet = this.client.getHabbo().getInventory().getPetsComponent().getPet(petId);

        if(pet == null)
        {
            return;
        }
        if(room.getCurrentPets().size() >= Room.MAXIMUM_PETS && !this.client.getHabbo().hasPermission("acc_unlimited_pets"))
        {
            this.client.sendResponse(new PetErrorComposer(PetErrorComposer.ROOM_ERROR_MAX_PETS));
            return;
        }

        int x = this.packet.readInt();
        int y = this.packet.readInt();

        RoomTile tile = null;
        RoomTile playerTile = this.client.getHabbo().getRoomUnit().getCurrentLocation();

        if ((x == 0 && y == 0) || !room.isOwner(this.client.getHabbo()))
        {
            //Place the pet in front of the player.
            tile = room.getLayout().getTileInFront(this.client.getHabbo().getRoomUnit().getCurrentLocation(), this.client.getHabbo().getRoomUnit().getBodyRotation().getValue());

            if (tile == null || !tile.isWalkable())
            {
                //Find a walkable tile around the player.
                for (RoomTile t : room.getLayout().getTilesAround(this.client.getHabbo().getRoomUnit().getCurrentLocation()))
                {
                    if (t != null && t.isWalkable())
                    {
                        tile = t;
                        break;
                    }
                }
            }

            //Check if tile exists and is walkable. Else place it in the current location the Habbo is standing.
            if (tile == null || !tile.isWalkable())
            {
                tile = playerTile;

                //If the current tile is not walkable, place it at the door.
                if (tile == null || !tile.isWalkable())
                {
                    tile = room.getLayout().getDoorTile();
                }
            }
        }
        else
        {
            tile = room.getLayout().getTile((short) x, (short) y);
        }

        if(tile == null || !tile.isWalkable() || !tile.allowStack())
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

        roomUnit.setPathFinderRoom(room);

        roomUnit.setLocation(tile);
        roomUnit.setZ(tile.getStackHeight());
        roomUnit.getStatus().put("sit", "0");
        roomUnit.setRoomUnitType(RoomUnitType.PET);
        if (playerTile != null)
        {
            roomUnit.lookAtPoint(playerTile);
        }
        pet.setRoomUnit(roomUnit);
        room.addPet(pet);
        pet.needsUpdate = true;
        Emulator.getThreading().run(pet);
        room.sendComposer(new RoomPetComposer(pet).compose());
        this.client.getHabbo().getInventory().getPetsComponent().removePet(pet);
        this.client.sendResponse(new RemovePetComposer(pet));
    }
}
