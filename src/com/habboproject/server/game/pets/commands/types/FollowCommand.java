package com.habboproject.server.game.pets.commands.types;

import com.habboproject.server.game.pets.commands.PetCommand;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;

public class FollowCommand extends PetCommand {
    @Override
    public boolean execute(PlayerEntity executor, PetEntity entity) {
        entity.moveTo(executor.getPosition().squareInFront(executor.getBodyRotation()));
        entity.getPetAI().free();

        entity.getPetAI().setFollowingPlayer(executor);

        executor.getFollowingEntities().add(entity);

        return false;
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
