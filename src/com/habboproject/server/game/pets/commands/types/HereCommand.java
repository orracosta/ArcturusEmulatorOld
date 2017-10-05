package com.habboproject.server.game.pets.commands.types;

import com.habboproject.server.game.pets.commands.PetCommand;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.misc.Position;

public class HereCommand extends PetCommand {
    @Override
    public boolean execute(PlayerEntity executor, PetEntity entity) {
        Position position = executor.getPosition().squareInFront(executor.getBodyRotation());

        entity.moveTo(position);

        entity.getRoom().getMapping().getTile(position).scheduleEvent(entity.getId(), (e) -> {
            if (e instanceof PetEntity) {
                ((PetEntity) e).getPetAI().sit();
                ((PetEntity) e).getPetAI().applyGesture("sml");

                ((PetEntity) e).getPetAI().increaseExperience(this.experienceGain());
            }

            e.setHeadRotation(Position.calculateRotation(position, executor.getPosition()));
            e.setBodyRotation(e.getHeadRotation());
            e.markNeedsUpdate();
        });

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

    @Override
    public int experienceGain() {
        return 10;
    }
}
