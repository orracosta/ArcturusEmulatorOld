package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionCannon;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

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
        
        THashSet<RoomTile> tiles = new THashSet<RoomTile>();
        int rotation = this.cannon.getRotation();
        
        // no_usage_dirs=5,6,7;fire_delay=750;load_delay=2000;dir=6;range=4;width=3;offset=1
            /*
        
        com.eu.habbo.habbohotel.users.HabboItem testItem = this.room.getHabboItem(48004);
        
        com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer testMsg = new com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer(testItem, null, 
                    room.getLayout().getTile((short)(this.cannon.getX() - 4), (short)(this.cannon.getY() - 1)),
                    (double)0,
                    room);
            
            room.sendComposer(
                testMsg.compose()
                );*/
        
        
        switch(rotation) {
            case 0:
                for(int i = 1; i <= 4; i++) {
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() - i), (short)(this.cannon.getY() - 1)));
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() - i), (short)(this.cannon.getY())));
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() - i), (short)(this.cannon.getY() + 1)));
                }
                break;
                
            case 2:
                for(int i = 1; i <= 4; i++) {
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() - 1), (short)(this.cannon.getY() - i)));
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX()), (short)(this.cannon.getY() - i)));
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() + 1), (short)(this.cannon.getY() - i)));
                }
                break;
                
            case 4:
                for(int i = 1; i <= 4; i++) {
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() + (i + 1)), (short)(this.cannon.getY() - 1)));
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() + (i + 1)), (short)(this.cannon.getY())));
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() + (i + 1)), (short)(this.cannon.getY() + 1)));
                }
                break;
                
            case 6:
                for(int i = 1; i <= 4; i++) {
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() - 1), (short)(this.cannon.getY() + (i + 1))));
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX()), (short)(this.cannon.getY() + (i + 1))));
                    tiles.add(room.getLayout().getTile((short)(this.cannon.getX() + 1), (short)(this.cannon.getY() + (i + 1))));
                }
                break;
        }

        for(RoomTile t : tiles)
        {
            for(Habbo habbo : this.room.getHabbosAt(t.x, t.y))
            {
                if(!habbo.hasPermission("acc_unkickable") && !this.room.isOwner(habbo)) {
                    Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this.room);
                    habbo.getClient().sendResponse(message); //kicked composer
                }
            }
        }
    }
}
