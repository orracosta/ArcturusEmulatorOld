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
        super("cmd_roomeffect", Emulator.getTexts().getValue("commands.keys.cmd_roomeffect").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length < 2)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_roomeffect.no_effect"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        try
        {
            int effectId = Integer.valueOf(params[1]);

            if(effectId >= 0)
            {
                for (Habbo habbo : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbos())
                {
                    habbo.getRoomUnit().setEffectId(effectId);
                    habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserEffectComposer(habbo.getRoomUnit()).compose());
                }

                return true;
            }
            else
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_roomeffect.positive"), RoomChatMessageBubbles.ALERT);
                return true;
            }
        }
        catch (Exception e)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_roomeffect.numbers_only"), RoomChatMessageBubbles.ALERT);
            return true;
        }
    }
}
