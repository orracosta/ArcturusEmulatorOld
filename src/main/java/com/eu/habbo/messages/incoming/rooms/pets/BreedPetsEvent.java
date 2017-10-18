package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.messages.incoming.MessageHandler;

public class BreedPetsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int unknownInt = this.packet.readInt(); //Something state. 2 = accept

        if (unknownInt == 0)
        {
            AbstractPet petOne = this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(this.packet.readInt());
            AbstractPet petTwo = this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(this.packet.readInt());

            if (petOne == null || petTwo == null)
            {
                //TODO Add error
                return;
            }

            if (petOne instanceof MonsterplantPet && petTwo instanceof MonsterplantPet)
            {
                ((MonsterplantPet) petOne).breed((MonsterplantPet) petTwo);
            }
        }
    }
}