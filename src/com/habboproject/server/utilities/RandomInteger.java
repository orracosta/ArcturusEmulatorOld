package com.habboproject.server.utilities;

import java.util.Random;


public class RandomInteger {
    private static final Random rand = new Random();

    public static Random getRandom() {
        return rand;
    }

    public static int getRandom(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public static int nextInt(int value) {
        return rand.nextInt(value);
    }
}
