package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

/**
 * Created on 4-11-2014 09:37.
 */
public class EnableCommand extends Command
{
    public EnableCommand()
    {
        super.permission = "cmd_enable";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_enable").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 2)
        {
            try
            {
                if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
                {
                    if(gameClient.getHabbo().getHabboInfo().getRiding() == null)
                    {
                        int effectId = Integer.parseInt(params[1]);

                        if (Emulator.getGameEnvironment().getPermissionsManager().isEffectBlocked(effectId, gameClient.getHabbo().getHabboInfo().getRank()))
                        {
                            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_enable.not_allowed"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                            return true;
                        }

                        gameClient.getHabbo().getRoomUnit().setEffectId(effectId);
                        gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserEffectComposer(gameClient.getHabbo().getRoomUnit()).compose());
                    }
                }
            }
            catch (Exception e)
            {
                //Don't handle incorrect parse exceptions :P
            }
        }
        return true;
    }
}
