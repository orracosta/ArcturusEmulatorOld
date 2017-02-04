package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.util.pathfinding.PathFinder;

public class RoomUnitRideHorse implements Runnable
{
    private HorsePet pet;
    private Habbo habbo;
    private RoomTile goalTile;

    public RoomUnitRideHorse(HorsePet pet, Habbo habbo, RoomTile goalTile)
    {
        this.pet = pet;
        this.habbo = habbo;
        this.goalTile = goalTile;
    }

    @Override
    public void run()
    {
        if(!(this.habbo.getRoomUnit() != null && this.habbo.getHabboInfo().getCurrentRoom() == this.pet.getRoom() && this.habbo.getHabboInfo().getRiding() == null))
            return;

        if(PathFinder.getSquareInFront(this.habbo.getHabboInfo().getCurrentRoom().getLayout(), this.habbo.getRoomUnit().getX(), this.habbo.getRoomUnit().getY(), this.habbo.getRoomUnit().getBodyRotation().getValue()).equals(this.goalTile))
        {
            if(this.goalTile.x == this.pet.getRoomUnit().getX() && this.goalTile.y == this.pet.getRoomUnit().getY())
            {
                this.habbo.getRoomUnit().setGoalLocation(this.pet.getRoomUnit().getCurrentLocation());
                this.habbo.getHabboInfo().getCurrentRoom().giveEffect(this.habbo, 77);
                this.habbo.getHabboInfo().setRiding(this.pet);
                this.pet.setRider(this.habbo);
                this.pet.setTask(PetTasks.RIDE);
            }
            else
            {
                Emulator.getThreading().run(this, 500);
            }
        }
    }
}
