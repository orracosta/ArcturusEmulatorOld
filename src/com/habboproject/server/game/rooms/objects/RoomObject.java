package com.habboproject.server.game.rooms.objects;

import com.habboproject.server.api.game.rooms.objects.IRoomObject;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.objects.misc.Positionable;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;


public abstract class RoomObject implements IRoomObject, Positionable {
    /**
     * The room where this object resides
     */
    private Room room;

    /**
     * The position on the grid this object resides
     */
    private Position position;

    /**
     * Create the room object instance
     *
     * @param position The position in the room where this object is
     * @param room     The room where this object is
     */
    public RoomObject(Position position, Room room) {
        this.position = position;
        this.room = room;
    }

    /**
     * Gets the tile instance from the room mapping
     *
     * @return the tile instance from the room mapping
     */
    public RoomTile getTile() {
        if (this.getPosition() == null) return null;

        return this.getRoom().getMapping().getTile(this.getPosition().getX(), this.getPosition().getY());
    }

    /**
     * Set the position to a new position
     *
     * @param newPosition The position to replace the current one with
     */
    public void setPosition(Position newPosition) {
        if (newPosition == null) return;

        this.position = newPosition.copy();
    }

    /**
     * Checks whether or not the object is at the door tile
     *
     * @return Is the object on the door tile?
     */
    public boolean isAtDoor() {
        return this.position.getX() == this.getRoom().getModel().getDoorX() && this.position.getY() == this.getRoom().getModel().getDoorY();
    }

    /**
     * Get the room where this object is
     *
     * @return The room instance
     */
    public Room getRoom() {
        return this.room;
    }

    /**
     * Get the position in which this object is on the grid
     *
     * @return The position instance
     */
    public Position getPosition() {
        return this.position;
    }
}
