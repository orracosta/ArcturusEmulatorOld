package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
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
                this.client.getHabbo().getHabboStats().petRespectPointsToGive--;
                pet.addExperience(10);
                pet.addRespect();
                this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomPetRespectComposer(pet).compose());
                AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("PetRespectGiver"));
                AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(pet.getUserId()), Emulator.getGameEnvironment().getAchievementManager().achievements.get("PetRespectReceiver"));

            }
        }
    }
}
