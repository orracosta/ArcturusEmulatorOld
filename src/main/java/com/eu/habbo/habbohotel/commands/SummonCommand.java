package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.HideDoorbellComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class SummonCommand extends Command
{
    public SummonCommand()
    {
        super("cmd_summon", Emulator.getTexts().getValue("commands.keys.cmd_summon").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return true;

        if(params.length >= 2)
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

            if(habbo == null)
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_summon.not_found").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if(gameClient.getHabbo().getHabboInfo().getUsername().equals(habbo.getHabboInfo().getUsername()))
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.generic.cmd_summon.self").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() == habbo.getHabboInfo().getCurrentRoom())
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.generic.cmd_summon.same_room").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
                return true;
            }

            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.generic.cmd_summon.summoning").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);

            //Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, habbo.getHabboInfo().getCurrentRoom());

            habbo.getClient().sendResponse(new ForwardToRoomComposer(gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId()));
            Emulator.getGameEnvironment().getRoomManager().enterRoom(habbo, gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId(), "", true);
            habbo.getClient().sendResponse(new HideDoorbellComposer(""));

            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_summon.summoned").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);

            RoomTile t = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTileInFront(gameClient.getHabbo().getRoomUnit().getCurrentLocation(), gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue());

            if(t != null && gameClient.getHabbo().getHabboInfo().getCurrentRoom().tileWalkable(t))
            {
                habbo.getRoomUnit().setGoalLocation(t);
            }

            habbo.getClient().sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.generic.cmd_summon.been_summoned").replace("%user%", gameClient.getHabbo().getHabboInfo().getUsername())));

            return true;
        }
        else
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_summon.forgot_username"), RoomChatMessageBubbles.ALERT);
            return true;
        }
    }
}
