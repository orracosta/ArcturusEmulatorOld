package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetTask;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;

public class PetFollowHabbo implements Runnable
{
    private final int directionOffset;
    private final Habbo habbo;
    private final Pet pet;

    public PetFollowHabbo(Pet pet, Habbo habbo, int offset)
    {
        this.pet = pet;
        this.habbo = habbo;
        this.directionOffset = offset;
    }

    @Override
    public void run()
    {
        if (this.pet != null)
        {
            if (this.pet.getTask() != PetTask.FOLLOW)
                return;

            if (this.habbo != null)
            {
                if (this.habbo.getRoomUnit() != null)
                {
                    if (this.pet.getRoomUnit() != null)
                    {
                        Tile target = PathFinder.getSquareInFront(this.habbo.getRoomUnit().getX(), this.habbo.getRoomUnit().getY(), Math.abs((this.habbo.getRoomUnit().getBodyRotation().getValue() + directionOffset + 4) % 8));

                        if (target.X < 0 || target.Y < 0)
                            target = PathFinder.getSquareInFront(this.habbo.getRoomUnit().getX(), this.habbo.getRoomUnit().getY(), this.habbo.getRoomUnit().getBodyRotation().getValue());

                        if (target.X >= 0 && target.Y >= 0)
                        {
                            if(this.pet.getRoom().getLayout().tileWalkable(target.X, target.Y))
                            {
                                this.pet.getRoomUnit().setGoalLocation(target);
                                this.pet.getRoomUnit().setCanWalk(true);
                                if (this.pet instanceof Pet)
                                {
                                    this.pet.setTask(PetTask.FOLLOW);
                                }
                            }
                        }
                        Emulator.getThreading().run(this, 500);
                    }
                }
            }
        }
    }
}
