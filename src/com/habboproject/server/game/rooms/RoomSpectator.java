package com.habboproject.server.game.rooms;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by brend on 04/02/2017.
 */
public class RoomSpectator {
    public static RoomSpectator instance;

    private final Map<Integer, List<Integer>> roomSpectators = Maps.newConcurrentMap();

    public static RoomSpectator getInstance() {
        if (instance == null) {
            instance = new RoomSpectator();
        }
        return instance;
    }

    public boolean hasSpectator(int roomId) {
        return this.roomSpectators.containsKey(roomId);
    }

    public List<Integer> getSpectatorsFromRoom(int roomId) {
        return this.roomSpectators.get(roomId);
    }

    public void addSpectatorsInRoom(int roomId) {
        if (this.hasSpectator(roomId)) {
            return;
        }

        this.roomSpectators.put(roomId, Lists.newArrayList());
    }

    public void removeSpectatorsFromRoom(int roomId) {
        if (!this.hasSpectator(roomId)) {
            return;
        }

        this.roomSpectators.remove(roomId);
    }

    public void removePlayerFromSpectateMode(int roomId, Integer playerId) {
        if (!this.hasSpectator(roomId)) {
            return;
        }

        if (!this.roomSpectators.get(roomId).contains(playerId)) {
            return;
        }

        this.roomSpectators.get(roomId).remove(playerId);

        if (this.getSpectatorsCount(roomId) <= 0) {
            this.removeSpectatorsFromRoom(roomId);
        }
    }

    public void addPlayerToSpectateMode(int roomId, int playerId) {
        if (!this.hasSpectator(roomId)) {
            return;
        }

        if (this.roomSpectators.get(roomId).contains(playerId)) {
            return;
        }

        this.getSpectatorsFromRoom(roomId).add(playerId);
    }

    public int getSpectatorsCount(int roomId) {
        if (!this.hasSpectator(roomId)) {
            return 0;
        }

        int size = 0;
        Iterator<Integer> iterator = this.getSpectatorsFromRoom(roomId).iterator();
        while (iterator.hasNext()) {
            int player = iterator.next();
            ++size;
        }

        return size;
    }
}
