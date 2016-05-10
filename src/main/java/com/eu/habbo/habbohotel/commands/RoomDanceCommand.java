package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDanceComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;

import java.util.NoSuchElementException;

public class RoomDanceCommand extends Command
{
    public RoomDanceCommand()
    {
        super("cmd_danceall", Emulator.getTexts().getValue("commands.keys.cmd_danceall").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 2)
        {
            int danceId;

            try
            {
                danceId = Integer.valueOf(params[1]);
            }
            catch (Exception e)
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_danceall.invalid_dance"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }

            if(danceId < 0 || danceId > 4)
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_danceall.outside_bounds"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
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
                    return true;
                }

                habboIterator.value().getRoomUnit().setDanceType(DanceType.values()[danceId]);
                habboIterator.value().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDanceComposer(habboIterator.value().getRoomUnit()).compose());
            }
        }
        else
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_danceall.no_dance"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }

        return true;
    }
}
