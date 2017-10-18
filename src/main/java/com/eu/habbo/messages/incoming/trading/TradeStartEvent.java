package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.trading.TradeStartFailComposer;

public class TradeStartEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int userId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null)
        {
            if (userId >= 0 && userId != this.client.getHabbo().getRoomUnit().getId())
            {
                Habbo targetUser = room.getHabboByRoomUnitId(userId);

                if (this.client.getHabbo().hasPermission("acc_trade_anywhere"))
                {
                    room.startTrade(this.client.getHabbo(), targetUser);
                    return;
                }

                if (!RoomTrade.TRADING_ENABLED)
                {
                    this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.HOTEL_TRADING_NOT_ALLOWED));
                    return;
                }

                if (room.getTradeMode() == 0 || (room.getTradeMode() == 1 && this.client.getHabbo().getHabboInfo().getId() != room.getOwnerId()))
                {
                    this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.ROOM_TRADING_NOT_ALLOWED));
                    return;
                }

                if (targetUser != null)
                {
                    if (!this.client.getHabbo().getRoomUnit().getStatus().containsKey("trd"))
                    {
                        if (this.client.getHabbo().getHabboStats().allowTrade)
                        {
                            if (!targetUser.getRoomUnit().getStatus().containsKey("trd"))
                            {
                                if (targetUser.getHabboStats().allowTrade)
                                {
                                    room.startTrade(this.client.getHabbo(), targetUser);
                                }
                                else
                                {
                                    this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.TARGET_TRADING_NOT_ALLOWED, targetUser.getHabboInfo().getUsername()));
                                }
                            }
                            else
                            {
                                this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.TARGET_ALREADY_TRADING, targetUser.getHabboInfo().getUsername()));
                            }
                        }
                        else
                        {
                            this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.YOU_TRADING_OFF));
                        }
                    }
                    else
                    {
                        this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.YOU_ALREADY_TRADING));
                    }
                }
            }
        }
    }
}
