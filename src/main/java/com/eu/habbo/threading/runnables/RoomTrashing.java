package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.set.hash.THashSet;

/**
 * Created on 7-8-2015 14:28.
 */
public class RoomTrashing implements Runnable
{
    public static RoomTrashing INSTANCE;

    private Habbo habbo;
    private Room room;

    public RoomTrashing(Habbo habbo, Room room)
    {
        this.habbo = habbo;
        this.room = room;

        RoomTrashing.INSTANCE = this;
    }

    @Override
    public void run()
    {

    }

    @EventHandler
    public static void onUserWalkEvent(UserTakeStepEvent event)
    {
        if(INSTANCE == null)
            return;

        if(INSTANCE.habbo == null)
            return;

        if(!INSTANCE.habbo.isOnline())
            INSTANCE.habbo = null;

        if(INSTANCE.habbo == event.habbo)
        {
            if(event.habbo.getHabboInfo().getCurrentRoom() != null)
            {
                if(event.habbo.getHabboInfo().getCurrentRoom().equals(INSTANCE.room))
                {
                    THashSet<ServerMessage> messages = new THashSet<ServerMessage>();

                    THashSet<HabboItem> items = INSTANCE.room.getItemsAt(event.toLocation.getX(), event.toLocation.getY());

                    int offset = Emulator.getRandom().nextInt(4) + 2;

                    Tile t = null;
                    while(offset > 0)
                    {
                        t = PathFinder.getSquareInFront(event.toLocation.getX(), event.toLocation.getY(), event.habbo.getRoomUnit().getBodyRotation().getValue(), offset);

                        if(!INSTANCE.room.getLayout().tileWalkable(t.X, t.Y))
                        {
                            offset--;
                        }
                        else
                        {
                            break;
                        }
                    }

                    for(HabboItem item : items)
                    {
                        t.Z = (INSTANCE.room.getTopHeightAt(t.X, t.Y));

                        messages.add(new FloorItemOnRollerComposer(item, null, t, INSTANCE.room).compose());
                    }


                    offset = Emulator.getRandom().nextInt(4) + 2;

                    t = null;
                    while(offset > 0)
                    {
                        t = PathFinder.getSquareInFront(event.toLocation.getX(), event.toLocation.getY(), event.habbo.getRoomUnit().getBodyRotation().getValue() + 7, offset);

                        if(!INSTANCE.room.getLayout().tileWalkable(t.X, t.Y))
                        {
                            offset--;
                        }
                        else
                        {
                            break;
                        }
                    }

                    Tile s = PathFinder.getSquareInFront(INSTANCE.habbo.getRoomUnit().getX(), INSTANCE.habbo.getRoomUnit().getY(), INSTANCE.habbo.getRoomUnit().getBodyRotation().getValue() + 7);
                    items = INSTANCE.room.getItemsAt(s.X, s.Y);

                    for(HabboItem item : items)
                    {
                        t.Z = (INSTANCE.room.getTopHeightAt(t.X, t.Y));

                        messages.add(new FloorItemOnRollerComposer(item, null, t, INSTANCE.room).compose());
                    }

                    offset = Emulator.getRandom().nextInt(4) + 2;

                    t = null;
                    while(offset > 0)
                    {
                        t = PathFinder.getSquareInFront(event.toLocation.getX(), event.toLocation.getY(), event.habbo.getRoomUnit().getBodyRotation().getValue() + 1, offset);

                        if(!INSTANCE.room.getLayout().tileWalkable(t.X, t.Y))
                        {
                            offset--;
                        }
                        else
                        {
                            break;
                        }
                    }

                    s = PathFinder.getSquareInFront(INSTANCE.habbo.getRoomUnit().getX(), INSTANCE.habbo.getRoomUnit().getY(), INSTANCE.habbo.getRoomUnit().getBodyRotation().getValue() + 1);
                    items = INSTANCE.room.getItemsAt(s.X, s.Y);

                    for(HabboItem item : items)
                    {
                        t.Z = (INSTANCE.room.getTopHeightAt(t.X, t.Y));

                        messages.add(new FloorItemOnRollerComposer(item, null, t, INSTANCE.room).compose());
                    }



                    for(ServerMessage message : messages)
                    {
                        INSTANCE.room.sendComposer(message);
                    }
                }
                else
                {
                    INSTANCE.habbo = null;
                    INSTANCE.room = null;
                }
            }
        }
    }

    public Habbo getHabbo()
    {
        return this.habbo;
    }

    public void setHabbo(Habbo habbo)
    {
        this.habbo = habbo;
    }

    public Room getRoom()
    {
        return this.room;
    }

    public void setRoom(Room room)
    {
        this.room = room;
    }
}
