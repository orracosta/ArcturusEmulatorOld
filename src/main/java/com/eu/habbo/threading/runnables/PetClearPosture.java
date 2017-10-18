package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetTasks;

public class PetClearPosture implements Runnable
{
    private final Pet pet;
    private final String key;
    private final PetTasks newTask;
    private final boolean clearTask;

    public PetClearPosture(Pet pet, String key, PetTasks newTask, boolean clearTask)
    {
        this.pet = pet;
        this.key = key;
        this.newTask = newTask;
        this.clearTask = clearTask;
    }

    @Override
    public void run()
    {
        if(this.pet != null)
        {
            if(this.pet.getRoom() != null)
            {
                if(this.pet.getRoomUnit() != null)
                {
                    this.pet.getRoomUnit().getStatus().remove(key);

                    if(this.pet instanceof Pet)
                        if(this.clearTask)
                            pet.setTask(PetTasks.FREE);
                        else
                            if(this.newTask != null)
                                this.pet.setTask(this.newTask);
                }
            }
        }
    }
}
