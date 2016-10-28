package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.inventory.RemoveHabboItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.AddFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.AddWallItemComposer;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurniturePlacedEvent;
import com.eu.habbo.util.pathfinding.PathFinder;
import gnu.trove.set.hash.THashSet;

public class RoomPlaceItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String[] values = this.packet.readString().split(" ");

        if(values.length < 1)
        {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_item"));
            return;
        }

        if(!this.client.getHabbo().getRoomUnit().isInRoom())
        {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
            return;
        }

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if(room == null)
        {
            return;
        }

        HabboItem rentSpace = null;
        if(!this.client.getHabbo().getHabboStats().canRentSpace())
        {
            rentSpace = room.getHabboItem(this.client.getHabbo().getHabboStats().rentedItemId);

//            if(rentSpace == null)
//            {
//                this.client.getHabbo().getHabboStats().setRentedTimeEnd(0);
//                this.client.getHabbo().getHabboStats().setRentedItemId(0);
//
//                return;
//            }
//            else
//            {
//                if(rentSpace instanceof InteractionRentableSpace)
//                {
//                    if(!((InteractionRentableSpace) rentSpace).isRented() || ((InteractionRentableSpace) rentSpace).getRenterId() != this.client.getHabbo().getHabboInfo().getId())
//                    {
//                        this.client.getHabbo().getHabboStats().setRentedTimeEnd(0);
//                        this.client.getHabbo().getHabboStats().setRentedItemId(0);
//
//                        return;
//                    }
//                }
//            }
        }
        else
        {
            HabboItem sp = room.getHabboItem(this.client.getHabbo().getHabboStats().rentedItemId);

            if(sp != null && sp instanceof InteractionRentableSpace)
            {
                if(((InteractionRentableSpace)sp).getRenterId() == this.client.getHabbo().getHabboInfo().getId())
                {
                    ((InteractionRentableSpace)sp).endRent();
                }
            }

            if (!room.hasRights(this.client.getHabbo()) && !this.client.getHabbo().hasPermission("acc_placefurni") && !(room.getGuildId() > 0 && room.guildRightLevel(this.client.getHabbo()) >= 2) && this.client.getHabbo().getHabboStats().canRentSpace())
            {
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
                return;
            }
        }

        int itemId = Integer.valueOf(values[0]);
        if(itemId <= 0)
            return;

        HabboItem item = this.client.getHabbo().getHabboInventory().getItemsComponent().getHabboItem(itemId);

        if(item == null)
            return;

        if(room.getId() != item.getRoomId() && item.getRoomId() != 0)
            return;

        if(item instanceof InteractionMoodLight && !room.getRoomSpecialTypes().getItemsOfType(InteractionMoodLight.class).isEmpty())
        {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "max_dimmers"));
            return;
        }

        THashSet<RoomTile> updatedTiles = new THashSet<RoomTile>();
        if(item.getBaseItem().getType().toLowerCase().equals("s"))
        {
            short x = Short.valueOf(values[1]);
            short y = Short.valueOf(values[2]);
            int rotation = Integer.valueOf(values[3]);

            if(x < 0 || y < 0 || rotation < 0)
            {
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                return;
            }

            if(rentSpace != null && !room.hasRights(this.client.getHabbo()))
            {
                if(item instanceof InteractionRoller ||
                        item instanceof InteractionStackHelper ||
                        item instanceof InteractionWired ||
                        item instanceof InteractionBackgroundToner ||
                        item instanceof InteractionRoomAds ||
                        item instanceof InteractionCannon ||
                        item instanceof InteractionPuzzleBox)
                {
                    this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
                    return;
                }
            }

            if(Emulator.getPluginManager().isRegistered(FurniturePlacedEvent.class, true))
            {
                Event furniturePlacedEvent = new FurniturePlacedEvent(item, this.client.getHabbo(), room.getLayout().getTile(x, y));
                Emulator.getPluginManager().fireEvent(furniturePlacedEvent);

                if(furniturePlacedEvent.isCancelled())
                    return;
            }

            if(rentSpace != null)
            {
                if(!PathFinder.squareInSquare(PathFinder.getSquare(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), PathFinder.getSquare(x, y, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation)))
                {
                    this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
                    return;
                }
            }

            double checkStackHeight = room.getStackHeight(x, y, true);
            HabboItem stackHelper = room.getStackHelper(x, y);

            if(stackHelper == null)
            {
                for (short i = x; i < x + item.getBaseItem().getWidth(); i++)
                {
                    for (short j = y; j < y + item.getBaseItem().getLength(); j++)
                    {
                        HabboItem topItem = room.getTopItemAt(i, j);
                        if (topItem != null && !topItem.getBaseItem().allowStack())
                        {
                            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                            return;
                        }

                        double testheight = room.getStackHeight(i, j, true);
                        if (checkStackHeight != testheight)
                        {
                            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                            return;
                        }
                        if (!room.getHabbosAt(i, j).isEmpty())
                        {
                            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                            return;
                        }

                        RoomTile t = room.getLayout().getTile(i, j);

                        if (t != null)
                        {
                            updatedTiles.add(t);
                        }
                    }
                }
            }

            item.setZ(stackHelper == null ? room.getStackHeight(x, y, false) : (stackHelper.getExtradata().isEmpty() ? 0.0D : (double)Integer.valueOf(stackHelper.getExtradata()) / 100));
            item.setX(x);
            item.setY(y);
            item.setRotation(rotation);
            room.sendComposer(new AddFloorItemComposer(item, this.client.getHabbo().getHabboInfo().getUsername()).compose());
            room.updateTiles(updatedTiles);
        }
        else
        {
            if(Emulator.getPluginManager().isRegistered(FurniturePlacedEvent.class, true))
            {
                Event furniturePlacedEvent = new FurniturePlacedEvent(item, this.client.getHabbo(), null);
                Emulator.getPluginManager().fireEvent(furniturePlacedEvent);

                if(furniturePlacedEvent.isCancelled())
                    return;
            }

            item.setWallPosition(values[1] + " " + values[2] + " " + values[3]);
            room.sendComposer(new AddWallItemComposer(item).compose());
        }

        this.client.sendResponse(new RemoveHabboItemComposer(item.getId()));
        item.needsUpdate(true);
        this.client.getHabbo().getHabboInventory().getItemsComponent().removeHabboItem(item.getId());
        room.addHabboItem(item);
        item.setRoomId(room.getId());

        if(!updatedTiles.isEmpty())
        {
            for (RoomTile t : updatedTiles)
            {
                t.setStackHeight(room.getStackHeight(t.x, t.y, false));
            }
            room.sendComposer(new UpdateStackHeightComposer(updatedTiles).compose());
        }
        Emulator.getThreading().run(item);
        item.onPlace(room);
    }
}
