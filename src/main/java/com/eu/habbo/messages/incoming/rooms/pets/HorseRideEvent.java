package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.threading.runnables.RoomUnitRideHorse;

public class HorseRideEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int petId = this.packet.readInt();

        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        AbstractPet pet = this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(petId);

        if(pet == null || !(pet instanceof HorsePet))
            return;

        if(this.client.getHabbo().getHabboInfo().getRiding() == null)
        {
            if (((HorsePet) pet).anyoneCanRide() || this.client.getHabbo().getHabboInfo().getId() == pet.getUserId())
            {
                if (((HorsePet) pet).getRider() != null)
                {
                    if (this.client.getHabbo().getHabboInfo().getId() == pet.getUserId())
                    {
                        this.client.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(((HorsePet) pet).getRider(), 0);
                        ((HorsePet) pet).getRider().getHabboInfo().setRiding(null);
                        ((HorsePet) pet).setRider(null);
                        ((HorsePet) pet).setTask(PetTasks.FREE);
                    } else
                    {
                        //TODO: Say somebody else is already riding.
                        return;
                    }
                }

                RoomTile goalTile = this.client.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTileInFront(this.client.getHabbo().getRoomUnit().getCurrentLocation(), this.client.getHabbo().getRoomUnit().getBodyRotation().getValue());

                if (goalTile != null)
                {
                    if (goalTile.equals(this.client.getHabbo().getRoomUnit().getCurrentLocation()))
                    {
                        this.client.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(this.client.getHabbo(), 77);
                        this.client.getHabbo().getHabboInfo().setRiding((HorsePet) pet);
                        ((HorsePet) pet).setRider(this.client.getHabbo());
                        this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserEffectComposer(this.client.getHabbo().getRoomUnit()).compose());
                        ((HorsePet) pet).setTask(PetTasks.RIDE);
                    }
                    else
                    {
                        pet.getRoomUnit().setGoalLocation(goalTile);
                        Emulator.getThreading().run(new RoomUnitRideHorse((HorsePet) pet, this.client.getHabbo(), goalTile));
                    }
                }
            }
        }
        else
        {
            this.client.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(this.client.getHabbo(), 0);
            ((HorsePet) pet).setRider(null);
            ((HorsePet) pet).setTask(PetTasks.FREE);
            this.client.getHabbo().getHabboInfo().setRiding(null);
        }
    }
}
