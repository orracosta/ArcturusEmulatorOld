package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

import java.util.Map;

public class SummonRankCommand extends Command
{
    public SummonRankCommand()
    {
        super("cmd_summonrank", Emulator.getTexts().getValue("commands.keys.cmd_summonrank").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        int minRank = 1;

        if(params.length >= 2)
        {
            try
            {
                minRank = Integer.valueOf(params[1]);
            }
            catch (Exception e)
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.generic.cmd_summonrank.error"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            for(Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet())
            {
                if(set.getValue().getHabboInfo().getRank().getId() >= minRank)
                {
                    if(set.getValue() == gameClient.getHabbo())
                        continue;

                    if (set.getValue().getHabboInfo().getCurrentRoom() == gameClient.getHabbo().getHabboInfo().getCurrentRoom())
                        continue;

                    Emulator.getGameEnvironment().getRoomManager().leaveRoom(set.getValue(), set.getValue().getHabboInfo().getCurrentRoom());
                    set.getValue().getClient().sendResponse(new ForwardToRoomComposer(gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId()));
                    Emulator.getGameEnvironment().getRoomManager().enterRoom(set.getValue(), gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId(), "", true);
                }
            }
        }

        return true;
    }
}
