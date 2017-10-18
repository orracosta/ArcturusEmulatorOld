package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.inventory.RemoveHabboItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.AddFloorItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.AddWallItemComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurniturePlacedEvent;
import gnu.trove.set.hash.THashSet;

import java.awt.*;

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

        HabboItem item = this.client.getHabbo().getInventory().getItemsComponent().getHabboItem(itemId);

        if(item == null)
            return;

        if(room.getId() != item.getRoomId() && item.getRoomId() != 0)
            return;

        if(item instanceof InteractionMoodLight && !room.getRoomSpecialTypes().getItemsOfType(InteractionMoodLight.class).isEmpty())
        {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "max_dimmers"));
            return;
        }

        if (item instanceof InteractionJukeBox && !room.getRoomSpecialTypes().getItemsOfType(InteractionJukeBox.class).isEmpty())
        {
            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "max_soundfurni"));
            return;
        }

        THashSet<RoomTile> updatedTiles = new THashSet<RoomTile>();
        if(item.getBaseItem().getType() == FurnitureType.FLOOR)
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
                if(!RoomLayout.squareInSquare(RoomLayout.getRectangle(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), RoomLayout.getRectangle(x, y, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation)))
                {
                    this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
                    return;
                }
            }

            double checkStackHeight = room.getStackHeight(x, y, true);

            if (checkStackHeight > 0 && item instanceof InteractionRoller)
            {
                this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                return;
            }

            HabboItem stackHelper = room.getStackHelper(x, y);

            Rectangle newSquare = RoomLayout.getRectangle(x, y, item.getBaseItem().getWidth(), item.getBaseItem().getLength(), rotation);

            //if (x != item.getX() || y != item.getY() || item.getRotation() != rotation)
            if (stackHelper == null)
            {
                checkStackHeight = room.getStackHeight(x, y, false, item);
                for (short i = (short) newSquare.x; i < newSquare.x + newSquare.getWidth(); i++)
                {
                    for (short j = (short) newSquare.y; j < newSquare.y + newSquare.getHeight(); j++)
                    {
                        double testheight = room.getStackHeight(i, j, false, item);
                        if (checkStackHeight != testheight && !(item instanceof InteractionStackHelper))
                        {
                            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                            this.client.sendResponse(new FloorItemUpdateComposer(item));
                            return;
                        }

                        if (!room.getHabbosAt(i, j).isEmpty() && !(item instanceof InteractionStackHelper || item.getBaseItem().allowSit()))
                        {
                            this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "${room.error.cant_set_item}"));
                            this.client.sendResponse(new FloorItemUpdateComposer(item));
                            return;
                        }

                        boolean found = false;
                        for (RoomTile tile : updatedTiles)
                        {
                            if (tile.x == i && tile.y == j)
                            {
                                found = true;
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

            item.setZ(stackHelper == null ? room.getStackHeight(x, y, false) : (stackHelper.getExtradata().isEmpty() ? room.getLayout().getHeightAtSquare(x, y) : (double) Integer.valueOf(stackHelper.getExtradata()) / 100));
            item.setX(x);
            item.setY(y);
            item.setRotation(rotation);
            room.sendComposer(new AddFloorItemComposer(item, this.client.getHabbo().getHabboInfo().getUsername()).compose());
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
            room.sendComposer(new AddWallItemComposer(item, this.client.getHabbo().getHabboInfo().getUsername()).compose());
        }

        Achievement roomDecoAchievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoFurniCount");
        int furniCollecterProgress = this.client.getHabbo().getHabboStats().getAchievementProgress(roomDecoAchievement);
        int difference = room.getUserFurniCount(this.client.getHabbo().getHabboInfo().getId()) - furniCollecterProgress;
        if (difference > 0)
        {
            AchievementManager.progressAchievement(this.client.getHabbo(), roomDecoAchievement, difference);
        }

        if (item instanceof InteractionBlackHole)
        {
            Achievement holeCountAchievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoHoleFurniCount");
            int holesCountProgress = this.client.getHabbo().getHabboStats().getAchievementProgress(holeCountAchievement);
            int holeDifference = room.getRoomSpecialTypes().getItemsOfType(InteractionBlackHole.class).size() - holesCountProgress;

            if (holeDifference > 0)
            {
                AchievementManager.progressAchievement(this.client.getHabbo(), holeCountAchievement, holeDifference);
            }
        }

        this.client.sendResponse(new RemoveHabboItemComposer(item.getId()));
        item.needsUpdate(true);
        this.client.getHabbo().getInventory().getItemsComponent().removeHabboItem(item.getId());
        room.addHabboItem(item);
        item.setRoomId(room.getId());

//        if(!updatedTiles.isEmpty())
//        {
//            for (RoomTile t : updatedTiles)
//            {
//                t.setStackHeight(room.getStackHeight(t.x, t.y, false));
//            }
//            room.sendComposer(new UpdateStackHeightComposer(updatedTiles).compose());
//        }
        room.updateTiles(updatedTiles);
        for (RoomTile tile : updatedTiles)
        {
            room.updateHabbosAt(tile.x, tile.y);
        }

        Emulator.getThreading().run(item);
        item.onPlace(room);
    }
}
