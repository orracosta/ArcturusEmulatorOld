package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureMovedEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureRotatedEvent;
import gnu.trove.set.hash.THashSet;

import java.awt.*;

public class RotateMoveItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null)
            return;

        int furniId = this.packet.readInt();
        HabboItem item = room.getHabboItem(furniId);

        HabboItem rentSpace = null;
        if(!this.client.getHabbo().getHabboStats().canRentSpace())
        {
            rentSpace = room.getHabboItem(this.client.getHabbo().getHabboStats().rentedItemId);
        }
        else
        {
            if (item == null || (!room.hasRights(this.client.getHabbo()) && !this.client.getHabbo().hasPermission("acc_placefurni") && !(room.getGuildId() > 0 && room.guildRightLevel(this.client.getHabbo()) >= 2)))
            {
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));

                if (item != null)
                {
                    this.client.sendResponse(new FloorItemUpdateComposer(item));
                }
                return;
            }
        }

        int x = this.packet.readInt();
        int y = this.packet.readInt();
        int rotation = this.packet.readInt();

        if(x < 0 || y < 0 || rotation < 0)
        {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
            return;
        }

        short oldX = item.getX();
        short oldY = item.getY();
        int oldRotation = item.getRotation();

        RoomTile oldLocation = room.getLayout().getTile(oldX, oldY);

        if(rentSpace != null)
        {
            if(!RoomLayout.squareInSquare(RoomLayout.getRectangle(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), RoomLayout.getRectangle(x, y, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation)))
            {
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
                this.client.sendResponse(new FloorItemUpdateComposer(item));
                return;
            }
        }

        HabboItem hasStackHelper = room.getStackHelper(x, y);
        HabboItem topItem = null;
        if(hasStackHelper == null)
            topItem = room.getTopItemAt(x, y);

        if (topItem != null && topItem != item && !topItem.getBaseItem().allowStack() && !(item instanceof InteractionStackHelper))
        {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
            this.client.sendResponse(new FloorItemUpdateComposer(item));
            return;
        }

        THashSet<RoomTile> updatedTiles = new THashSet<RoomTile>();

        Rectangle currentSquare = RoomLayout.getRectangle(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

        for (short i = (short) currentSquare.x; i < currentSquare.x + currentSquare.getWidth(); i++)
        {
            for (short j = (short) currentSquare.y; j < currentSquare.y + currentSquare.getHeight(); j++)
            {
                RoomTile tile = room.getLayout().getTile(i, j);

                if (tile != null)
                {
                    updatedTiles.add(tile);
                }
            }
        }

//        if ((x != item.getX() || y != item.getY()) || item.getRotation() != rotation) {
//            for (int i = item.getX(); i < item.getX() + item.getBaseItem().getWidth(); i++) {
//                for (int j = item.getY(); j < item.getY() + item.getBaseItem().getLength(); j++) {
//                    updatedTiles.add(new Tile(i, j, 0));
//                }
//            }
//        }

        //room.removeHabboItem(item.getId());

        double checkStackHeight = item.getZ();

        Rectangle newSquare = RoomLayout.getRectangle(x, y, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);

        //if (x != item.getX() || y != item.getY() || item.getRotation() != rotation)
        if (hasStackHelper == null)
        {
            checkStackHeight = room.getStackHeight(x, y, false, item);
            for (short i = (short) newSquare.x; i < newSquare.x + newSquare.getWidth(); i++)
            {
                for (short j = (short) newSquare.y; j < newSquare.y + newSquare.getHeight(); j++)
                {
                    double testheight = room.getStackHeight(i, j, false, item);
                    if (
                            (checkStackHeight != testheight && !(item instanceof InteractionStackHelper)) ||
                            (!room.getHabbosAt(i, j).isEmpty() && !(oldX == x && oldY == y) && !(item instanceof InteractionStackHelper)) ||
                            (checkStackHeight > 0 && item instanceof InteractionRoller)
                        )
                    {
                        this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                        this.client.sendResponse(new FloorItemUpdateComposer(item));
                        return;
                    }

                    boolean found = false;
                    for (RoomTile tile : updatedTiles)
                    {
                        if (tile != null)
                        {
                            if (tile.x == i && tile.y == j)
                            {
                                found = true;
                            }
                        }
                    }

                    if (!found)
                    {
                        RoomTile t = room.getLayout().getTile(i, j);
                        if (t != null)
                            updatedTiles.add(t);
                    }
                }
            }
        }

        if(topItem == null || (topItem != item && topItem.getBaseItem().allowStack())) {
            item.setZ(topItem == null && hasStackHelper != null ? hasStackHelper.getExtradata().isEmpty() ? Double.valueOf("0.0") : Double.valueOf(hasStackHelper.getExtradata()) / 100.0D : checkStackHeight);
        }

        if(item.getBaseItem().allowSit())
        {
            THashSet<Habbo> habbos = room.getHabbosAt(oldX, oldY);
            THashSet<RoomUnit> updatedUnits = new THashSet<RoomUnit>();
            for (Habbo habbo : habbos)
            {
                habbo.getRoomUnit().getStatus().remove("sit");
            }
            room.sendComposer(new RoomUserStatusComposer(updatedUnits, false).compose());
        }

        if (item.getBaseItem().allowLay())
        {
            THashSet<Habbo> habbos = room.getHabbosAt(oldX, oldY);
            THashSet<RoomUnit> updatedUnits = new THashSet<RoomUnit>();
            for (Habbo habbo : habbos)
            {
                habbo.getRoomUnit().getStatus().remove("lay");
            }
            room.sendComposer(new RoomUserStatusComposer(updatedUnits, false).compose());
        }

        item.setX((short) x);
        item.setY((short) y);

        if(item.getRotation() != rotation)
        {
            item.setRotation(rotation);

            if(Emulator.getPluginManager().isRegistered(FurnitureRotatedEvent.class, true))
            {
                Event furnitureRotatedEvent = new FurnitureRotatedEvent(item, this.client.getHabbo(), oldRotation);
                Emulator.getPluginManager().fireEvent(furnitureRotatedEvent);

                if(furnitureRotatedEvent.isCancelled())
                {
                    item.setRotation(oldRotation);
                }
            }
        }

        RoomTile newLocation = room.getLayout().getTile(item.getX(), item.getY());
        item.onMove(room, oldLocation, newLocation);

        if(Emulator.getPluginManager().isRegistered(FurnitureMovedEvent.class, true))
        {
            Event furnitureMovedEvent = new FurnitureMovedEvent(item, this.client.getHabbo(), oldLocation, newLocation);
            Emulator.getPluginManager().fireEvent(furnitureMovedEvent);

            if(furnitureMovedEvent.isCancelled())
                return;
        }

        if (item instanceof InteractionStackHelper)
        {
            try
            {
                item.setZ(Double.valueOf(item.getExtradata()) / 100.0);
            }
            catch (Exception e)
            {}
        }

        room.updateTiles(updatedTiles);

        for(RoomTile t : updatedTiles)
        {
            //t.setStackHeight(room.getStackHeight(t.x, t.y, false));

            if(oldX != t.x || oldY != t.y)
                room.updateHabbosAt(t.x, t.y);
        }

        if(oldX != x || oldY != y)
            room.updateHabbosAt(oldX, oldY);

        room.sendComposer(new FloorItemUpdateComposer(item).compose());
        item.needsUpdate(true);
        Emulator.getThreading().run(item);
    }
}