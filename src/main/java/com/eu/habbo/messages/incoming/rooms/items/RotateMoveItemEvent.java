package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureMovedEvent;
import com.eu.habbo.plugin.events.furniture.FurniturePlacedEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureRotatedEvent;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.set.hash.THashSet;

import java.awt.*;

/**
 * Created on 20-9-2014 21:06.
 */
public class RotateMoveItemEvent extends MessageHandler {
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

            //if(rentSpace == null)
            //    return;
        }
        else
        {
            if (item == null || (!room.hasRights(this.client.getHabbo()) && !this.client.getHabbo().hasPermission("acc_placefurni") && !(room.getGuildId() > 0 && room.guildRightLevel(this.client.getHabbo()) >= 2)))
            {
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
                this.client.sendResponse(new FloorItemUpdateComposer(item));
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

        int oldX = item.getX();
        int oldY = item.getY();
        int oldRotation = item.getRotation();

        Tile oldLocation = new Tile(oldX, oldY, item.getZ());
        Tile newLocation = new Tile(oldX, oldY, 0.0);
        if(rentSpace != null)
        {
            if(!PathFinder.squareInSquare(PathFinder.getSquare(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), PathFinder.getSquare(x, y, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation)))
            {
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
                this.client.sendResponse(new FloorItemUpdateComposer(item));
                return;
            }
        }

        THashSet<Tile> updatedTiles = new THashSet<Tile>();

        Rectangle currentSquare = PathFinder.getSquare(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

        for (int i = currentSquare.x; i < currentSquare.x + currentSquare.getWidth(); i++)
        {
            for (int j = currentSquare.y; j < currentSquare.y + currentSquare.getHeight(); j++)
            {
                updatedTiles.add(new Tile(i, j, 0));
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

        Rectangle newSquare = PathFinder.getSquare(x, y, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);

        if (x != item.getX() || y != item.getY() || item.getRotation() != rotation)
        {
            checkStackHeight = room.getStackHeight(x, y, false, item);
            for (int i = newSquare.x; i < newSquare.x + newSquare.getWidth(); i++)
            {
                for (int j = newSquare.y; j < newSquare.y + newSquare.getHeight(); j++)
                {
                    double testheight = room.getStackHeight(i, j, false, item);
                    if (checkStackHeight != testheight && !(item instanceof InteractionStackHelper))
                    {
                        this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                        this.client.sendResponse(new FloorItemUpdateComposer(item));
                        return;
                    }

                    if (room.getHabbosAt(i, j).size() > 0 && !item.getBaseItem().allowSit() && !(item instanceof InteractionStackHelper))
                    {
                        this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                        this.client.sendResponse(new FloorItemUpdateComposer(item));
                        return;
                    }

                    Tile t = null;
                    for (Tile tile : updatedTiles)
                    {
                        if (tile.X != i || tile.Y != j)
                        {
                            t = new Tile(i, j, 0);
                        }
                    }
                    if (t != null)
                        updatedTiles.add(t);
                }
            }
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

        HabboItem hasStackHelper = room.getStackHelper(x, y);
        HabboItem topItem = null;
        if(hasStackHelper == null)
            topItem = room.getTopItemAt(x, y);

        if(topItem == null || (topItem != item && topItem.getBaseItem().allowStack())) {
            item.setZ(topItem == null && hasStackHelper != null ? hasStackHelper.getExtradata().isEmpty() ? Double.valueOf("0.0") : Double.valueOf(hasStackHelper.getExtradata()) / 100.0D : checkStackHeight);
        }

        item.setX(x);
        item.setY(y);

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

        item.onMove(oldLocation, newLocation);

        if(Emulator.getPluginManager().isRegistered(FurnitureMovedEvent.class, true))
        {
            Event furnitureMovedEvent = new FurnitureMovedEvent(item, this.client.getHabbo(), oldLocation, newLocation);
            Emulator.getPluginManager().fireEvent(furnitureMovedEvent);

            if(furnitureMovedEvent.isCancelled())
                return;
        }

        room.sendComposer(new FloorItemUpdateComposer(item).compose());

        for(Tile t : updatedTiles)
        {
            t.Z = room.getStackHeight(t.X, t.Y, true);
            room.updateHabbosAt(t.X, t.Y);
        }

        room.updateTiles(updatedTiles);
        room.sendComposer(new UpdateStackHeightComposer(updatedTiles).compose());
        item.needsUpdate(true);
        Emulator.getThreading().run(item);
    }
}
