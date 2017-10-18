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
            String message = "";
            for (int i = 1; i < params.length; i++)
            {
                message += params[i] + " ";
            }

            Emulator.getGameEnvironment().getHabboManager().staffAlert(message + "\r\n-" + gameClient.getHabbo().getHabboInfo().getUsername());
            Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new FriendChatMessageComposer(new Message(gameClient.getHabbo().getHabboInfo().getId(), -1, message)).compose(), "acc_staff_chat", gameClient);
        }
        else
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_staffalert.forgot_message"), RoomChatMessageBubbles.ALERT);
        }

        return true;
    }
}
