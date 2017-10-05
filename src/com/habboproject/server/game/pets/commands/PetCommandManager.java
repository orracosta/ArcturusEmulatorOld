package com.habboproject.server.game.pets.commands;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.pets.commands.types.*;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class PetCommandManager {
    private static PetCommandManager petCommandManager;

    private Map<String, PetCommand> petCommands;

    public PetCommandManager() {
        this.initialize();
    }

    public void initialize() {
        if(this.petCommands != null) {
            this.petCommands.clear();
        }

        petCommands = new HashMap<String, PetCommand>() {{
            put(Locale.getOrDefault("game.pet.command.sit", "sit"), new SitCommand());
            put(Locale.getOrDefault("game.pet.command.free", "free"), new FreeCommand());
            put(Locale.getOrDefault("game.pet.command.here", "here"), new HereCommand());
            put(Locale.getOrDefault("game.pet.command.follow", "follow"), new FollowCommand());
            put(Locale.getOrDefault("game.pet.command.play", "play"), new PlayCommand());
            put(Locale.getOrDefault("game.pet.command.play_dead", "play dead"), new PlayDeadCommand());
            put(Locale.getOrDefault("game.pet.command.stay", "stay"), new StayCommand());
            put(Locale.getOrDefault("game.pet.command.breed", "breed"), new BreedCommand());
        }};
    }

    public boolean executeCommand(String commandKey, PlayerEntity executor, PetEntity petEntity) {
        if(!this.petCommands.containsKey(commandKey)) {
            return false;
        }

        PetCommand command = this.petCommands.get(commandKey);

        if(command.getRequiredLevel() > petEntity.getData().getLevel()) {
            // too low of a level!
            return false;
        }

        if(command.requiresOwner() && executor.getPlayerId() != petEntity.getData().getOwnerId())
            return false;

        return command.execute(executor, petEntity);
    }

    public static PetCommandManager getInstance() {
        if (petCommandManager == null)
            petCommandManager = new PetCommandManager();

        return petCommandManager;
    }
}
