package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;

/**
 * Created on 23-7-2015 14:48.
 */
public class TestPetCommand extends Command
{
    public TestPetCommand()
    {
        super.permission = "acc_debug";
        super.keys = new String[] { "pettest" };
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length < 3)
            return false;

        String petName = params[1];
        String look = params[2].replace("-", " ");

        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
        if(room == null)
            return false;

        for(AbstractPet pet : room.getCurrentPets().valueCollection())
        {
            if(pet.getName().equalsIgnoreCase(petName))
            {
                if(pet instanceof MonsterplantPet)
                {
                    ((MonsterplantPet) pet).look = look;
                    room.sendComposer(new RoomPetComposer(pet).compose());
                }
            }
        }

        return true;
    }
}
