package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class EnableCommand extends Command
{
    public EnableCommand()
    {
        super("cmd_enable", Emulator.getTexts().getValue("commands.keys.cmd_enable").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length >= 2)
        {
            int effectId = 0;
            try
            {
                effectId = Integer.parseInt(params[1]);
            }
            catch (Exception e)
            {
                return false;
            }
            Habbo target = gameClient.getHabbo();
            if (params.length == 3)
            {
                target = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(params[2]);
            }

            if (target != null)
            {
                if (target == gameClient.getHabbo() || gameClient.getHabbo().hasPermission("acc_enable_others"))
                {
                    try
                    {
                        if (target.getHabboInfo().getCurrentRoom() != null)
                        {
                            if (target.getHabboInfo().getRiding() == null)
                            {
                                if (Emulator.getGameEnvironment().getPermissionsManager().isEffectBlocked(effectId, target.getHabboInfo().getRank().getId()))
                                {
                                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_enable.not_allowed"), RoomChatMessageBubbles.ALERT);
                                    return true;
                                }

                                target.getHabboInfo().getCurrentRoom().giveEffect(target, effectId);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }
}
