package com.habboproject.server.api.rooms;

import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.RoomData;


public class RoomStats {
    /**
     * The data of the room
     */
    private RoomData data;

    /**
     * The amount of players in the room
     */
    private int players;

    /**
     * The amount of bots in the room
     */
    private int bots;

    /**
     * The amount of pets in the room
     */
    private int pets;

    /**
     * The time the room was initially loaded
     */
    private long loadTime;

    /**
     * Initialize the RoomStats object
     *
     * @param room The room
     */
    public RoomStats(Room room) {
        this.data = room.getData();
        this.players = room.getEntities().playerCount();
        this.bots = room.getEntities().getBotEntities().size();
        this.pets = room.getEntities().getPetEntities().size();

        this.loadTime = (long) room.getAttribute("loadTime");
    }

    /**
     * Get the data of the room
     *
     * @return The data of the room
     */
    public RoomData getData() {
        return data;
    }

    /**
     * Set the data of the room
     *
     * @param data The data of the room
     */
    public void setData(RoomData data) {
        this.data = data;
    }

    /**
     * Get the amount of players in the room
     *
     * @return The amount of players in the room
     */
    public int getPlayers() {
        return players;
    }

    /**
     * Set the amount of players in the room
     *
     * @param players The amount of players in the room
     */
    public void setPlayers(int players) {
        this.players = players;
    }

    /**
     * Get the amount of bots in the room
     *
     * @return The amount of bots in the room
     */
    public int getBots() {
        return bots;
    }

    /**
     * Set the amount of bots in the room
     *
     * @param bots The amount of bots in the room
     */
    public void setBots(int bots) {
        this.bots = bots;
    }

    /**
     * Get the amount of pets in the room
     *
     * @return The amount of pets in the room
     */
    public int getPets() {
        return pets;
    }

    /**
     * Set the amount of pets in the room
     *
     * @param pets The amount of pets in the room
     */
    public void setPets(int pets) {
        this.pets = pets;
    }

    /**
     * Get the time the room was loaded
     *
     * @return The time the room was loaded
     */
    public long getLoadTime() {
        return loadTime;
    }

    /**
     * Set the time the room was loaded
     *
     * @param loadTime The time the room was loaded
     */
    public void setLoadTime(long loadTime) {
        this.loadTime = loadTime;
    }
}
