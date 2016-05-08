package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.messages.outgoing.trading.*;

import java.util.ArrayList;
import java.util.List;

public class RoomTrade
{
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
            this.tradeCompleted = true;
            sendMessageToUsers(new TradeCompleteComposer());
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
            tradeItems();
            closeWindow();
            this.room.stopTrade(this);
        }
    }

    void tradeItems()
    {
        for(RoomTradeUser roomTradeUser : this.users)
        {
            for(HabboItem item : roomTradeUser.getItems())
            {
                if(roomTradeUser.getHabbo().getHabboInventory().getItemsComponent().getHabboItem(item.getId()) == null)
                {
                    sendMessageToUsers(new TradeClosedComposer(roomTradeUser.getHabbo().getRoomUnit().getId(), TradeClosedComposer.ITEMS_NOT_FOUND));
                    return;
                }
            }
        }

        RoomTradeUser userOne = this.users.get(0);
        RoomTradeUser userTwo = this.users.get(1);

        for(HabboItem item : userOne.getItems())
        {
            item.setUserId(userTwo.getHabbo().getHabboInfo().getId());
            userOne.getHabbo().getHabboInventory().getItemsComponent().removeHabboItem(item);
            userTwo.getHabbo().getHabboInventory().getItemsComponent().addItem(item);
            item.needsUpdate(true);
            Emulator.getThreading().run(item);
        }

        for(HabboItem item : userTwo.getItems())
        {
            item.setUserId(userOne.getHabbo().getHabboInfo().getId());
            userTwo.getHabbo().getHabboInventory().getItemsComponent().removeHabboItem(item);
            userOne.getHabbo().getHabboInventory().getItemsComponent().addItem(item);
            item.needsUpdate(true);
            Emulator.getThreading().run(item);
        }

        userOne.getHabbo().getClient().sendResponse(new AddHabboItemComposer(userTwo.getItems()));
        userTwo.getHabbo().getClient().sendResponse(new AddHabboItemComposer(userOne.getItems()));

        userOne.getHabbo().getClient().sendResponse(new InventoryRefreshComposer());
        userTwo.getHabbo().getClient().sendResponse(new InventoryRefreshComposer());

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
        removeStatusses();
        sendMessageToUsers(new TradeClosedComposer(habbo.getHabboInfo().getId(), TradeClosedComposer.USER_CANCEL_TRADE));
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
