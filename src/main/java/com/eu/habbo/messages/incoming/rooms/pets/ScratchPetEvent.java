package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetRespectComposer;

public class ScratchPetEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int petId = this.packet.readInt();

        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        if(this.client.getHabbo().getHabboStats().petRespectPointsToGive > 0)
        {
            Pet pet = (Pet)this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(petId);

            if(pet != null)
            {
                pet.scratched(this.client.getHabbo());
            }
        }
    }
}
