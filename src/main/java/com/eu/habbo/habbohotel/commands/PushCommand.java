package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 21-2-2015 10:49.
 */
public class PushCommand extends Command
{
    public PushCommand()
    {
        super.permission = "cmd_push";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_push").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 2)
        {
            Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(params[1]);

            if(habbo == null)
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_push.not_found").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
            else if(habbo == gameClient.getHabbo())
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_push.push_self"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
            else
            {
                Tile tFront = PathFinder.getSquareInFront(gameClient.getHabbo().getRoomUnit().getX(), gameClient.getHabbo().getRoomUnit().getY(), gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue());

                if(tFront.X == habbo.getRoomUnit().getX() && tFront.Y == habbo.getRoomUnit().getY())
                {
                    habbo.getRoomUnit().setGoalLocation(PathFinder.getSquareInFront(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY(), gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue()));
                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserTalkComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_push.push").replace("%user%", params[1]).replace("%gender_name%", (gameClient.getHabbo().getHabboInfo().getGender().equals(HabboGender.M) ? Emulator.getTexts().getValue("gender.him") : Emulator.getTexts().getValue("gender.her"))), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.NORMAL)).compose());
                }
                else
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_push.cant_reach").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
            }
            return true;
        }

        return true;
    }
}
