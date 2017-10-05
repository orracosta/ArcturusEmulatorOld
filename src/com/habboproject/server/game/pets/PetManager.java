package com.habboproject.server.game.pets;

import com.habboproject.server.game.pets.data.PetSpeech;
import com.habboproject.server.game.pets.races.PetRace;
import com.habboproject.server.storage.queries.pets.PetDao;
import com.habboproject.server.utilities.Initializable;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class PetManager implements Initializable {
    private static PetManager petManagerInstance;
    private Logger log = Logger.getLogger(PetManager.class.getName());

    private List<PetRace> petRaces;

    private Map<Integer, PetSpeech> petMessages;

    private Map<String, String> transformablePets;

    public PetManager() {

    }

    @Override
    public void initialize() {
        this.loadPetRaces();
        this.loadPetSpeech();
        this.loadTransformablePets();

        log.info("PetManager initialized");
    }

    public static PetManager getInstance() {
        if (petManagerInstance == null)
            petManagerInstance = new PetManager();

        return petManagerInstance;
    }

    public void loadPetRaces() {
        if (this.petRaces != null) {
            this.petRaces.clear();
        }

        try {
            this.petRaces = PetDao.getRaces();

            log.info("Loaded " + this.petRaces.size() + " pet races");
        } catch (Exception e) {
            log.error("Error while loading pet races", e);
        }
    }

    public void loadPetSpeech() {
        if (this.petMessages != null) {
            this.petMessages.clear();
        }

        try {
            AtomicInteger petSpeechCount = new AtomicInteger(0);
            this.petMessages = PetDao.getMessages(petSpeechCount);

            log.info("Loaded " + this.petMessages.size() + " pet message sets and " + petSpeechCount.get() + " total messages");
        } catch(Exception e) {
            log.error("Error while loading pet messages");
        }
    }

    public void loadTransformablePets() {
        if (this.transformablePets != null) {
            this.transformablePets.clear();
        }

        try {
            this.transformablePets = PetDao.getTransformablePets();

            log.info("Loaded " + this.transformablePets.size() + " transformable pets");
        } catch(Exception e) {
            log.error("Error while loading transformable pets");
        }
    }

    public int validatePetName(String petName) {
        String pattern = "^[a-zA-Z0-9]*$";

        if (petName.length() <= 0) {
            return 1;
        }

        if (petName.length() > 16) {
            return 2;
        }

        if (!petName.matches(pattern)) {
            return 3;
        }

        // WORD FILTER

        return 0;
    }

    public List<PetRace> getRacesByRaceId(int raceId) {
        List<PetRace> races = new ArrayList<>();

        for (PetRace race : this.getPetRaces()) {
            if (raceId == race.getRaceId())
                races.add(race);
        }

        return races;
    }

    public List<PetRace> getPetRaces() {
        return this.petRaces;
    }

    public PetSpeech getSpeech(int petType) {
        return this.petMessages.get(petType);
    }

    public Map<String, String> getTransformablePets() {
        return transformablePets;
    }

    public String getTransformationData(String type) {
        return this.transformablePets.get(type);
    }

//    public String[] getSpeech(int petType) {
//        return this.petSpeech.get(petType);
//    }
}
