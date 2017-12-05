package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.messages.outgoing.trading.*;
import gnu.trove.set.hash.THashSet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTrade
{
    //Configuration. Loaded from database & updated accordingly.
    public static boolean TRADING_ENABLED = true;

    private final List<RoomTradeUser> users;
    private boolean tradeCompleted;
    private final Room room;

    public RoomTrade(Habbo userOne, Habbo userTwo, Room room)
    {
        this.users = new ArrayList<RoomTradeUser>();
        this.tradeCompleted = false;

        this.users.add(new RoomTradeUser(userOne));
        this.users.add(new RoomTradeUser(userTwo));
        this.room = room;


        for(RoomTradeUser roomTradeUser : this.users)
        {
            if(!roomTradeUser.getHabbo().getRoomUnit().getStatus().containsKey("trd"))
            {
                roomTradeUser.getHabbo().getRoomUnit().getStatus().put("trd", "");
                if(!roomTradeUser.getHabbo().getRoomUnit().isWalking())
                    room.sendComposer(new RoomUserStatusComposer(roomTradeUser.getHabbo().getRoomUnit()).compose());
            }
        }

        sendMessageToUsers(new TradeStartComposer(this));
    }

    public void offerItem(Habbo habbo, HabboItem item)
    {
        RoomTradeUser user = getRoomTradeUserForHabbo(habbo);

        if(user.getItems().contains(item))
            return;

        user.getItems().add(item);

        clearAccepted();
        updateWindow();
    }
    
    public void offerMultipleItems(Habbo habbo, THashSet<HabboItem> items)
    {
        RoomTradeUser user = getRoomTradeUserForHabbo(habbo);

        for(HabboItem item : items) 
        {
            if(!user.getItems().contains(item)) 
            {
                user.getItems().add(item);
            }
        }

        clearAccepted();
        updateWindow();
    }

    public void removeItem(Habbo habbo, HabboItem item)
    {
        RoomTradeUser user = getRoomTradeUserForHabbo(habbo);

        if(!user.getItems().contains(item))
            return;

        user.getItems().remove(item);

        clearAccepted();
        updateWindow();
    }

    public void accept(Habbo habbo, boolean value)
    {
        RoomTradeUser user = getRoomTradeUserForHabbo(habbo);

        user.setAccepted(value);

        sendMessageToUsers(new TradeAcceptedComposer(user));
        boolean accepted = true;
        for(RoomTradeUser roomTradeUser : this.users)
        {
            if(!roomTradeUser.getAccepted())
                accepted = false;
        }
        if(accepted)
        {
            sendMessageToUsers(new TradingWaitingConfirmComposer());
        }
    }

    public void confirm(Habbo habbo)
    {
        RoomTradeUser user = getRoomTradeUserForHabbo(habbo);

        user.confirm();

        sendMessageToUsers(new TradeAcceptedComposer(user));
        boolean accepted = true;
        for(RoomTradeUser roomTradeUser : this.users)
        {
            if(!roomTradeUser.getConfirmed())
                accepted = false;
        }
        if(accepted)
        {
            if (tradeItems())
            {
                closeWindow();
                sendMessageToUsers(new TradeCompleteComposer());
            }
            this.room.stopTrade(this);
        }
    }

    boolean tradeItems()
    {
        for(RoomTradeUser roomTradeUser : this.users)
        {
            for(HabboItem item : roomTradeUser.getItems())
            {
                if(roomTradeUser.getHabbo().getInventory().getItemsComponent().getHabboItem(item.getId()) == null)
                {
                    sendMessageToUsers(new TradeClosedComposer(roomTradeUser.getHabbo().getRoomUnit().getId(), TradeClosedComposer.ITEMS_NOT_FOUND));
                    return false;
                }
            }
        }

        RoomTradeUser userOne = this.users.get(0);
        RoomTradeUser userTwo = this.users.get(1);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {

            int tradeId = 0;

            boolean logTrades = Emulator.getConfig().getBoolean("hotel.log.trades");
            if (logTrades)
            {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO room_trade_log (user_one_id, user_two_id, user_one_ip, user_two_ip, timestamp, user_one_item_count, user_two_item_count) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
                {
                    statement.setInt(1, userOne.getHabbo().getHabboInfo().getId());
                    statement.setInt(2, userTwo.getHabbo().getHabboInfo().getId());
                    statement.setString(3, userOne.getHabbo().getHabboInfo().getIpLogin());
                    statement.setString(4, userTwo.getHabbo().getHabboInfo().getIpLogin());
                    statement.setInt(5, Emulator.getIntUnixTimestamp());
                    statement.setInt(6, userOne.getItems().size());
                    statement.setInt(7, userTwo.getItems().size());
                    statement.executeUpdate();
                    try (ResultSet generatedKeys = statement.getGeneratedKeys())
                    {
                        if (generatedKeys.next())
                        {
                            tradeId = generatedKeys.getInt(1);
                        }
                    }
                }
            }

            int userOneId = userOne.getHabbo().getHabboInfo().getId();
            int userTwoId = userTwo.getHabbo().getHabboInfo().getId();

            try (PreparedStatement statement = connection.prepareStatement("UPDATE items SET user_id = ? WHERE id = ? LIMIT 1"))
            {
                try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO room_trade_log_items (id, item_id, user_id) VALUES (?, ?, ?)"))
                {
                    for (HabboItem item : userOne.getItems())
                    {
                        item.setUserId(userTwoId);
                        statement.setInt(1, userTwoId);
                        statement.setInt(2, item.getId());
                        userOne.getHabbo().getInventory().getItemsComponent().removeHabboItem(item);
                        userTwo.getHabbo().getInventory().getItemsComponent().addItem(item);
                        statement.addBatch();

                        if (logTrades)
                        {
                            stmt.setInt(1, tradeId);
                            stmt.setInt(2, item.getId());
                            stmt.setInt(3, userOneId);
                            stmt.addBatch();
                        }
                    }

                    for (HabboItem item : userTwo.getItems())
                    {
                        item.setUserId(userOneId);
                        statement.setInt(1, userOneId);
                        statement.setInt(2, item.getId());
                        userTwo.getHabbo().getInventory().getItemsComponent().removeHabboItem(item);
                        userOne.getHabbo().getInventory().getItemsComponent().addItem(item);
                        statement.addBatch();

                        if (logTrades)
                        {
                            stmt.setInt(1, tradeId);
                            stmt.setInt(2, item.getId());
                            stmt.setInt(3, userTwoId);
                            stmt.addBatch();
                        }
                    }

                    if (logTrades)
                    {
                        stmt.executeBatch();
                    }
                }

                statement.executeBatch();
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        userOne.getHabbo().getClient().sendResponse(new AddHabboItemComposer(userTwo.getItems()));
        userTwo.getHabbo().getClient().sendResponse(new AddHabboItemComposer(userOne.getItems()));

        userOne.getHabbo().getClient().sendResponse(new InventoryRefreshComposer());
        userTwo.getHabbo().getClient().sendResponse(new InventoryRefreshComposer());
        return true;
    }

    void clearAccepted()
    {
        for(RoomTradeUser user : this.users)
        {
            user.setAccepted(false);
        }
    }

    void updateWindow()
    {
        this.sendMessageToUsers(new TradeUpdateComposer(this));
    }

    private void closeWindow()
    {
        removeStatusses();

        sendMessageToUsers(new TradeCloseWindowComposer());
    }

    public void stopTrade(Habbo habbo)
    {
        this.removeStatusses();
        this.clearAccepted();
        for (RoomTradeUser user : this.users)
        {
            user.clearItems();
        }
        this.updateWindow();
        this.sendMessageToUsers(new TradeClosedComposer(habbo.getHabboInfo().getId(), TradeClosedComposer.USER_CANCEL_TRADE));
        this.room.stopTrade(this);
    }

    private void removeStatusses()
    {
        for(RoomTradeUser user : this.users)
        {
            Habbo habbo = user.getHabbo();

            if(habbo == null)
                continue;

            habbo.getRoomUnit().getStatus().remove("trd");
            this.room.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
        }
    }

    public RoomTradeUser getRoomTradeUserForHabbo(Habbo habbo)
    {
        for(RoomTradeUser roomTradeUser : this.users)
        {
            if(roomTradeUser.getHabbo() == habbo)
                return roomTradeUser;
        }
        return null;
    }

    void sendMessageToUsers(MessageComposer message)
    {
        for(RoomTradeUser roomTradeUser : this.users)
        {
            roomTradeUser.getHabbo().getClient().sendResponse(message);
        }
    }

    public List<RoomTradeUser> getRoomTradeUsers()
    {
        return this.users;
    }
}
