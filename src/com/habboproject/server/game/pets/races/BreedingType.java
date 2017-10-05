package com.habboproject.server.game.pets.races;

/**
 * Created by brend on 18/03/2017.
 */
public enum  BreedingType {
    DOG(0),
    CAT(1),
    TERRIER(3),
    BEAR(4),
    PIG(5);

    private final int breedingId;

    BreedingType(int breedingId) {
        this.breedingId = breedingId;
    }

    public int getBreedingId() {
        return breedingId;
    }

    public static BreedingType getType(int typeId) {
        switch (typeId) {
            case 0: // dog
                return BreedingType.DOG;

            case 1: // cat
                return BreedingType.CAT;

            case 3: // terrier
                return BreedingType.TERRIER;

            case 4: // bear
                return BreedingType.BEAR;

            case 5: // pig
                return BreedingType.PIG;
        }

        return null;
    }
}
