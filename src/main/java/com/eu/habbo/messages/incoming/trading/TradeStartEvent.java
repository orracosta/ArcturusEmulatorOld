package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.Room;
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

                if (targetUser != null)
                {
                    if (this.client.getHabbo().getRoomUnit().getStatus().containsKey("trd"))
                    {
                        this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.YOU_ALREADY_TRADING));
                        return;
                    }

                    if (!this.client.getHabbo().getHabboStats().allowTrade)
                    {
                        this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.YOU_TRADING_OFF));
                        return;
                    }

                    if (targetUser.getRoomUnit().getStatus().containsKey("trd"))
                    {
                        this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.TARGET_ALREADY_TRADING));
                        return;
                    }

                    if (!targetUser.getHabboStats().allowTrade)
                    {
                        this.client.sendResponse(new TradeStartFailComposer(TradeStartFailComposer.TARGET_TRADING_NOT_ALLOWED));
                        return;
                    }

                    room.startTrade(this.client.getHabbo(), targetUser);
                }
            }
        }
    }
}
