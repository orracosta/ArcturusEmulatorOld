package com.habboproject.server.game.pets.commands.types;

import com.habboproject.server.game.pets.commands.PetCommand;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;

/**
 * Created by brend on 19/03/2017.
 */
public class BreedCommand extends PetCommand {
    @Override
    public boolean execute(PlayerEntity executor, PetEntity entity) {
        entity.getPetAI().breed();

        return true;
    }

    @Override
    public int getRequiredLevel() {
        return 0;
    }

    @Override
    public boolean requiresOwner() {
        return true;
    }
}
