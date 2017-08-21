package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserIgnoredComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class UnmuteCommand extends Command
{
    public UnmuteCommand()
    {
        super("cmd_unmute", Emulator.getTexts().getValue("commands.keys.cmd_unmute").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 1)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_unmute.not_specified"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

        if(habbo == null)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_unmute.not_found").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }
        else
        {
            if (!habbo.getHabboStats().allowTalk() || habbo.getHabboInfo().getCurrentRoom().isMuted(habbo))
            {
                if (!habbo.getHabboStats().allowTalk())
                {
                    habbo.unMute();
                }

                if (habbo.getHabboInfo().getCurrentRoom().isMuted(habbo))
                {
                    habbo.getHabboInfo().getCurrentRoom().muteHabbo(habbo, 0);
                }

                if (habbo.getHabboInfo().getCurrentRoom() != null)
                {
                    habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserIgnoredComposer(habbo, RoomUserIgnoredComposer.UNIGNORED).compose());
                }

                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_unmute").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            }
            else
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_unmute.not_muted").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
        }

        return true;
    }
}