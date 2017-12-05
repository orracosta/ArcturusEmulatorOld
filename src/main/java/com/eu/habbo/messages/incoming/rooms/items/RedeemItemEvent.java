package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import com.eu.habbo.messages.outgoing.users.UserCurrencyComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurnitureRedeemedEvent;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;

public class RedeemItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room != null)
        {
            HabboItem item = room.getHabboItem(itemId);

            if(item != null && this.client.getHabbo().getHabboInfo().getId() == item.getUserId())
            {
                boolean furnitureRedeemEventRegistered = Emulator.getPluginManager().isRegistered(FurnitureRedeemedEvent.class, true);

                if(item.getBaseItem().getName().startsWith("CF_") || item.getBaseItem().getName().startsWith("CFC_") || item.getBaseItem().getName().startsWith("DF_") || item.getBaseItem().getName().startsWith("PF_"))
                {
                    if ((item.getBaseItem().getName().startsWith("CF_") || item.getBaseItem().getName().startsWith("CFC_")) && !item.getBaseItem().getName().contains("_diamond_"))
                    {
                        int credits;
                        try
                        {
                            credits = Integer.valueOf(item.getBaseItem().getName().split("_")[1]);
                        }
                        catch (Exception e)
                        {
                            Emulator.getLogging().logErrorLine("Failed to parse redeemable furniture: " + item.getBaseItem().getName() + ". Must be in format of CF_<amount>");
                            return;
                        }

                        if(furnitureRedeemEventRegistered)
                        {
                            Event furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), credits, FurnitureRedeemedEvent.CREDITS);
                            Emulator.getPluginManager().fireEvent(furniRedeemEvent);

                            if(furniRedeemEvent.isCancelled())
                                return;
                        }

                        this.client.getHabbo().getHabboInfo().addCredits(credits);
                        this.client.sendResponse(new UserCreditsComposer(this.client.getHabbo()));

                    } else if (item.getBaseItem().getName().startsWith("PF_"))
                    {
                        int pixels;

                        try
                        {
                            pixels = Integer.valueOf(item.getBaseItem().getName().split("_")[1]);
                        }
                        catch (Exception e)
                        {
                            Emulator.getLogging().logErrorLine("Failed to parse redeemable pixel furniture: " + item.getBaseItem().getName() + ". Must be in format of PF_<amount>");
                            return;
                        }

                        if(furnitureRedeemEventRegistered)
                        {
                            Event furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), pixels, FurnitureRedeemedEvent.PIXELS);
                            Emulator.getPluginManager().fireEvent(furniRedeemEvent);

                            if(furniRedeemEvent.isCancelled())
                                return;
                        }

                        this.client.getHabbo().getHabboInfo().addPixels(pixels);
                        this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));
                    }
                    else if (item.getBaseItem().getName().startsWith("DF_"))
                    {
                        int pointsType;
                        int points;

                        try
                        {
                            pointsType = Integer.valueOf(item.getBaseItem().getName().split("_")[1]);
                        }
                        catch (Exception e)
                        {
                            Emulator.getLogging().logErrorLine("Failed to parse redeemable points furniture: " + item.getBaseItem().getName() + ". Must be in format of DF_<pointstype>_<amount> where <pointstype> equals integer representation of seasonal currency.");
                            return;
                        }

                        try
                        {
                            points = Integer.valueOf(item.getBaseItem().getName().split("_")[2]);
                        }
                        catch (Exception e)
                        {
                            Emulator.getLogging().logErrorLine("Failed to parse redeemable points furniture: " + item.getBaseItem().getName() + ". Must be in format of DF_<pointstype>_<amount> where <pointstype> equals integer representation of seasonal currency.");
                            return;
                        }

                        if(furnitureRedeemEventRegistered)
                        {
                            Event furniRedeemEvent = new FurnitureRedeemedEvent(item, this.client.getHabbo(), points, FurnitureRedeemedEvent.DIAMONDS);
                            Emulator.getPluginManager().fireEvent(furniRedeemEvent);

                            if(furniRedeemEvent.isCancelled())
                                return;
                        }

                        this.client.getHabbo().givePoints(pointsType, points);
                    }
                    else if (item.getBaseItem().getName().startsWith("CF_diamond_"))
                    {
                        try
                        {
                            this.client.getHabbo().givePoints(Integer.valueOf(item.getBaseItem().getName().split("_")[2]));
                        }
                        catch (Exception e)
                        {
                            Emulator.getLogging().logErrorLine("Failed to parse redeemable diamonds furniture: " + item.getBaseItem().getName() + ". Must be in format of CF_diamond_<amount>");
                            return;
                        }
                    }

                    room.removeHabboItem(item);
                    room.sendComposer(new RemoveFloorItemComposer(item).compose());
                    RoomTile t = room.getLayout().getTile(item.getX(), item.getY());
                    t.setStackHeight(room.getStackHeight(item.getX(), item.getY(), false));
                    room.updateTile(t);
                    room.sendComposer(new UpdateStackHeightComposer(item.getX(), item.getY(), t.relativeHeight()).compose());
                    Emulator.getThreading().run(new QueryDeleteHabboItem(item));
                }
            }
        }
    }
}
