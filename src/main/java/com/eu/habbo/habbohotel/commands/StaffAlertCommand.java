package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.messenger.Message;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.friends.FriendChatMessageComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class StaffAlertCommand extends Command
{
    public StaffAlertCommand()
    {
        super("cmd_staffalert", Emulator.getTexts().getValue("commands.keys.cmd_staffalert").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length > 1)
        {
            String message = Emulator.getTexts().getValue("commands.generic.cmd_staffalert.title") + "\r\n";

            for (int i = 1; i < params.length; i++)
            {
                message += params[i] + " ";
            }

            ServerMessage msg = new GenericAlertComposer(message + "\r\n-" + gameClient.getHabbo().getHabboInfo().getUsername()).compose();

            Emulator.getGameEnvironment().getHabboManager().sendPacketToHabbosWithPermission(msg, "cmd_staffalert");
        }
        else
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_staffalert.forgot_message"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }

        return true;
    }
}
