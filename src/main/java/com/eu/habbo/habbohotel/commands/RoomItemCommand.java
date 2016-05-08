package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserHandItemComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;

import java.util.NoSuchElementException;

public class RoomItemCommand extends Command
{
    public RoomItemCommand()
    {
        super.permission = "cmd_roomitem";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_roomitem").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        int itemId = 0;

        if(params.length >= 2)
        {
            try
            {
                itemId = Integer.valueOf(params[1]);

                if(itemId < 0)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_roomitem.positive"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
            }
            catch (Exception e)
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_roomitem.no_item"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
        }

        TIntObjectMap<Habbo> habboList = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentHabbos();
        TIntObjectIterator<Habbo> habboIterator = habboList.iterator();

        for (int i = habboList.size(); i-- > 0; )
        {
            try
            {
                habboIterator.advance();
            } catch (NoSuchElementException e)
            {
                break;
            }

            habboIterator.value().getRoomUnit().setHandItem(itemId);
            habboIterator.value().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserHandItemComposer(habboIterator.value().getRoomUnit()).compose());
        }

        if(itemId > 0)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_roomitem.given").replace("%item%", itemId + ""), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }
        else
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_roomitem.removed"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }
    }
}
