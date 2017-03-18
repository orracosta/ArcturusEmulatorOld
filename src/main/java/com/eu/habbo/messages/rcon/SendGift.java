package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SendGift extends RCONMessage<SendGift.SendGiftJSON>
{
    /**
     * Sends a gift to an user.
     */
    public SendGift()
    {
        super(SendGiftJSON.class);
    }

    @Override
    public void handle(Gson gson, SendGiftJSON json)
    {
        if (json.userid < 0 && json.username.isEmpty())
        {
            this.status = RCONMessage.STATUS_ERROR;
            this.message = Emulator.getTexts().getValue("commands.error.cmd_gift.user_not_found").replace("%username%", json.username);
            return;
        }

        if(json.itemid < 0)
        {
            this.status = RCONMessage.STATUS_ERROR;
            this.message = Emulator.getTexts().getValue("commands.error.cmd_gift.not_a_number");
            return;
        }

        Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(json.itemid);
        if(baseItem == null)
        {
            this.status = RCONMessage.STATUS_ERROR;
            this.message = Emulator.getTexts().getValue("commands.error.cmd_gift.not_found").replace("%itemid%", json.itemid + "");
            return;
        }

        boolean userFound;
        Habbo habbo;

        if (json.userid >= 0)
        {
            habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.userid);
        }
        else
        {
            habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.userid);
        }

        userFound = habbo != null;
        if (!userFound)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE " + (json.username.isEmpty() ? "id = ?" : "username = ?") + " LIMIT 1"))
            {
                if (json.username.isEmpty())
                {
                    statement.setInt(1, json.userid);
                }
                else
                {
                    statement.setString(1, json.username);
                }

                try (ResultSet set = statement.executeQuery())
                {
                    if (set.next())
                    {
                        json.username = set.getString("username");
                        json.userid = set.getInt("id");
                        userFound = true;
                    }
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
        else
        {
            json.userid = habbo.getHabboInfo().getId();
            json.username = habbo.getHabboInfo().getUsername();
        }

        if (!userFound)
        {
            this.status = RCONMessage.STATUS_ERROR;
            this.message = Emulator.getTexts().getValue("commands.error.cmd_gift.user_not_found").replace("%username%", json.username);
            return;
        }

        HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(0, baseItem, 0, 0, "");
        Item giftItem = Emulator.getGameEnvironment().getItemManager().getItem((Integer)Emulator.getGameEnvironment().getCatalogManager().giftFurnis.values().toArray()[Emulator.getRandom().nextInt(Emulator.getGameEnvironment().getCatalogManager().giftFurnis.size())]);

        String extraData = "1\t" + item.getId();
        extraData += "\t0\t0\t0\t"+ json.message +"\t0\t0";

        Emulator.getGameEnvironment().getItemManager().createGift(json.username, giftItem, extraData, 0, 0);

        this.message = Emulator.getTexts().getValue("commands.succes.cmd_gift").replace("%username%", json.username).replace("%itemname%", item.getBaseItem().getName());

        if (habbo != null)
        {
            habbo.getClient().sendResponse(new InventoryRefreshComposer());
        }
    }

    public class SendGiftJSON
    {
        public int userid = -1;
        public String username = "";
        public int itemid = -1;
        public String message = "";
    }
}