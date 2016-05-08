package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.AddPetComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

/**
 * Created on 30-11-2014 14:54.
 */
public class PetPickupEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int petId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        Pet pet = (Pet)room.getPet(petId);

        if(this.client.getHabbo().getHabboInfo().getId() == pet.getId() || room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_anyroomowner"))
        {
            if(this.client.getHabbo().getHabboInventory().getPetsComponent().getPets().size() >= Emulator.getConfig().getInt("hotel.pets.max.inventory") && !this.client.getHabbo().hasPermission("acc_unlimited_pets"))
            {
                this.client.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("error.pets.max.inventory"), this.client.getHabbo(), this.client.getHabbo(), RoomChatMessageBubbles.ALERT)));

                //TODO: Add message: Max inventory reached
                //error.pets.max.inventory
                return;
            }
            room.sendComposer(new RoomUserRemoveComposer(pet.getRoomUnit()).compose());
            room.removePet(petId);
            pet.setRoomUnit(null);
            pet.setRoom(null);
            pet.needsUpdate = true;

            Emulator.getThreading().run(pet);

            if (this.client.getHabbo().getHabboInfo().getId() == pet.getUserId())
            {
                this.client.sendResponse(new AddPetComposer(pet));
                this.client.getHabbo().getHabboInventory().getPetsComponent().addPet(pet);
            }
            else
            {
                Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(pet.getUserId());

                if(habbo != null)
                {
                    habbo.getClient().sendResponse(new AddPetComposer(pet));
                    habbo.getHabboInventory().getPetsComponent().addPet(pet);
                }
            }
        }
    }
}
