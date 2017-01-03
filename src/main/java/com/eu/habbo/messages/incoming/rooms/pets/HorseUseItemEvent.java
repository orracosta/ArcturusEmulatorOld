package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetHorseFigureComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;

public class HorseUseItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        HabboItem item = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

        if(item == null)
            return;

        int petId = this.packet.readInt();
        AbstractPet pet = this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(petId);

        if(pet instanceof Pet)
        {
            if(pet instanceof HorsePet)
            {
                if(item.getBaseItem().getName().toLowerCase().startsWith("horse_dye"))
                {
                    int race = Integer.valueOf(item.getBaseItem().getName().split("_")[2]);
                    int raceType = (race * 4) - 2;

                    if(race >= 13 && race <= 17)
                        raceType = ((2 + race) * 4) + 1;

                    if(race == 0)
                        raceType = 0;

                    pet.setRace(raceType);
                    ((HorsePet) pet).needsUpdate = true;
                }
                else if(item.getBaseItem().getName().toLowerCase().startsWith("horse_hairdye"))
                {
                    int splittedHairdye = Integer.valueOf(item.getBaseItem().getName().toLowerCase().split("_")[2]);
                    int newHairdye = 48;

                    if(splittedHairdye == 0)
                    {
                        newHairdye = -1;
                    }
                    else if(splittedHairdye == 1)
                    {
                        newHairdye = 1;
                    }
                    else if(splittedHairdye >= 13 && splittedHairdye <= 17)
                    {
                        newHairdye = 68 + splittedHairdye;
                    }
                    else
                    {
                        newHairdye += splittedHairdye;
                    }

                    ((HorsePet) pet).setHairColor(newHairdye);
                    ((HorsePet) pet).needsUpdate = true;
                }
                else if(item.getBaseItem().getName().toLowerCase().startsWith("horse_hairstyle"))
                {
                    int splittedHairstyle = Integer.valueOf(item.getBaseItem().getName().toLowerCase().split("_")[2]);
                    int newHairstyle = 100;

                    if(splittedHairstyle == 0)
                    {
                        newHairstyle = -1;
                    }
                    else
                    {
                        newHairstyle += splittedHairstyle;
                    }

                    ((HorsePet) pet).setHairStyle(newHairstyle);
                    ((HorsePet) pet).needsUpdate = true;
                }
                else if(item.getBaseItem().getName().toLowerCase().startsWith("horse_saddle"))
                {
                    ((HorsePet) pet).hasSaddle(true);
                    ((HorsePet) pet).needsUpdate = true;
                }

                if(((HorsePet) pet).needsUpdate)
                {
                    Emulator.getThreading().run(pet);
                    this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomPetHorseFigureComposer((HorsePet) pet).compose());
                    this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RemoveFloorItemComposer(item).compose());
                    Emulator.getThreading().run(new QueryDeleteHabboItem(item));
                }
            }
        }
    }
}