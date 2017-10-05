package com.habboproject.server.game.rooms;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by brend on 05/02/2017.
 */
public class RoomUtil {
    public static <T> T getRandomElement(List<T> elements) {
        int size = elements.size();
        if (size > 0) {
            return elements.get(ThreadLocalRandom.current().nextInt(size));
        }

        return null;
    }
}
