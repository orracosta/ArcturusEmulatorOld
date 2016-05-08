package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;

import java.util.NoSuchElementException;

public class RoomEffectCommand extends Command
{
    public RoomEffectCommand()
    {
        super.permission = "cmd_roomeffect";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_roomeffect").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length < 2)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_roomeffect.no_effect"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        try
        {
            int effectId = Integer.valueOf(params[1]);

            if(effectId >= 0)
            {
                TIntObjectMap<Habbo> habboList = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentHabbos();
                TIntObjectIterator<Habbo> habboIterator = habboList.iterator();

                for (int i = habboList.size(); i-- > 0; )
                {
                    try
                    {
                        habboIterator.advance();
                    } catch (NoSuchElementException e)
                    {
                        return true;
                    }

                    habboIterator.value().getRoomUnit().setEffectId(effectId);
                    habboIterator.value().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserEffectComposer(habboIterator.value().getRoomUnit()).compose());
                }

                return true;
            }
            else
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_roomeffect.positive"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
        }
        catch (Exception e)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_roomeffect.numbers_only"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }
    }
}
