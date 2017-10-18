package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.ClothItem;
import com.eu.habbo.habbohotel.items.interactions.InteractionClothing;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.messages.outgoing.users.UserClothesComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RedeemClothingEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() != null &&
                this.client.getHabbo().getHabboInfo().getCurrentRoom().hasRights(this.client.getHabbo()))
        {
            HabboItem item = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

            if(item.getUserId() == this.client.getHabbo().getHabboInfo().getId())
            {
                if(item instanceof InteractionClothing)
                {
                    ClothItem clothing = Emulator.getGameEnvironment().getCatalogManager().getClothing(item.getBaseItem().getName());

                    if (clothing != null)
                    {
                        if (!this.client.getHabbo().getInventory().getWardrobeComponent().getClothing().contains(clothing.id))
                        {
                            item.setRoomId(0);
                            this.client.getHabbo().getHabboInfo().getCurrentRoom().removeHabboItem(item);
                            this.client.getHabbo().getHabboInfo().getCurrentRoom().updateTile(this.client.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTile(item.getX(), item.getY()));
                            this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RemoveFloorItemComposer(item, true).compose());
                            Emulator.getThreading().run(new QueryDeleteHabboItem(item));

                            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users_clothing (user_id, clothing_id) VALUES (?, ?)"))
                            {
                                statement.setInt(1, this.client.getHabbo().getHabboInfo().getId());
                                statement.setInt(2, clothing.id);
                                statement.execute();
                            }
                            catch (SQLException e)
                            {
                                Emulator.getLogging().logSQLException(e);
                            }

                            this.client.getHabbo().getInventory().getWardrobeComponent().getClothing().add(clothing.id);
                            this.client.sendResponse(new UserClothesComposer(this.client.getHabbo()));
                        }
                        else
                        {
                            this.client.sendResponse(new GenericAlertComposer("You already own this item!"));
                        }
                    }
                    else
                    {
                        Emulator.getLogging().logErrorLine("[Catalog] No definition in catalog_clothing found for clothing name " + item.getBaseItem().getName() + ". Could not redeem clothing!");
                    }
                }
            }
        }
    }
}
