package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionCannon;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.map.hash.THashMap;

public class CannonKickAction implements Runnable
{
    private final InteractionCannon cannon;
    private final Room room;

    public CannonKickAction(InteractionCannon cannon, Room room)
    {
        this.cannon = cannon;
        this.room = room;
    }

    @Override
    public void run()
    {
        THashMap<String, String> dater = new THashMap<String, String>();
        dater.put("title", "${notification.room.kick.cannonball.title}");
        dater.put("message", "${notification.room.kick.cannonball.message}");

        ServerMessage message = new BubbleAlertComposer("cannon.png", dater).compose();

        int rotation = this.cannon.getRotation();
        Tile a = PathFinder.getSquareInFront(this.cannon.getX(), this.cannon.getY(), rotation + 6);
        Tile b = PathFinder.getSquareInFront(a.X, a.Y, rotation);
        Tile c = PathFinder.getSquareInFront(b.X, b.Y, rotation);

        Tile[] tiles = {a, b, c};

        for(Tile t : tiles)
        {
            for(Habbo habbo : this.room.getHabbosAt(t.X, t.Y))
            {
                Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this.room);
                habbo.getClient().sendResponse(message); //kicked composer
            }
        }
    }
}
