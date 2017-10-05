package com.habboproject.server.game.rooms;

import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.room.queue.RoomQueueStatusMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomQueue {
    private static RoomQueue instance;

    private final Map<Integer, List<Integer>> roomQueues;

    public RoomQueue() {
        this.roomQueues = Maps.newConcurrentMap();

        this.roomQueues.put(1, new ArrayList<>());
    }

    public boolean hasQueue(int roomId) {
        return this.roomQueues.containsKey(roomId);
    }

    public List<Integer> getQueue(int roomId) {
        return this.roomQueues.get(roomId);
    }

    public void addQueue(int roomId, int startingPlayer) {
        this.roomQueues.put(roomId, startingPlayer == 0 ? new ArrayList<>() : new ArrayList<>(startingPlayer));
    }

    public void removeQueue(int roomId) {
        this.roomQueues.remove(roomId);
    }

    public void removePlayerFromQueue(int roomId, Integer playerId) {
        if (this.hasQueue(roomId)) {
            this.roomQueues.get(roomId).remove(playerId);

            for(int player : this.roomQueues.get(roomId)) {
                Session session = NetworkManager.getInstance().getSessions().getByPlayerId(player);

                if(session != null) {
                    session.send(new RoomQueueStatusMessageComposer(this.getQueueCount(roomId, player), RoomSpectator.getInstance().getSpectatorsCount(roomId)));
                }
            }
        }
    }

    public void addPlayerToQueue(int roomId, int playerId) {
        if (!this.hasQueue(roomId)) {
            return;
        }

        this.getQueue(roomId).add(playerId);
    }

    public int getNextPlayer(int roomId) {
        if (!this.hasQueue(roomId)) {
            return 0;
        }

        return this.getQueue(roomId).get(0);
    }

    public int getQueueCount(int roomId, int playerId) {
        if (!this.hasQueue(roomId)) {
            return 0;
        }

        int size = 0;

        for (int player : this.getQueue(roomId)) {
            if (player == playerId) {
                break;
            }

            size++;
        }

        return size;
    }

    public static RoomQueue getInstance() {
        if (instance == null) {
            instance = new RoomQueue();
        }

        return instance;
    }
}
