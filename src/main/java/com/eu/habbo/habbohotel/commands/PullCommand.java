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

public class PullCommand extends Command
{
    public PullCommand()
    {
        super("cmd_pull", Emulator.getTexts().getValue("commands.keys.cmd_pull").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 2)
        {
            Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(params[1]);

            if(habbo == null)
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_pull.not_found").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
            else if(habbo == gameClient.getHabbo())
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_pull.pull_self"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
            else
            {
                int distanceX = habbo.getRoomUnit().getX() - gameClient.getHabbo().getRoomUnit().getX();
                int distanceY = habbo.getRoomUnit().getY() - gameClient.getHabbo().getRoomUnit().getY();

                if(distanceX < -2 || distanceX > 2 || distanceY < -2 || distanceY > 2)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_pull.cant_reach").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
                else
                {
                    habbo.getRoomUnit().setGoalLocation(PathFinder.getSquareInFront(gameClient.getHabbo().getRoomUnit().getX(), gameClient.getHabbo().getRoomUnit().getY(), gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue()));
                    gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserTalkComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_pull.pull").replace("%user%", params[1]).replace("%gender_name%", (gameClient.getHabbo().getHabboInfo().getGender().equals(HabboGender.M) ? Emulator.getTexts().getValue("gender.him") : Emulator.getTexts().getValue("gender.her"))), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.NORMAL)).compose());
                }
            }
            return true;
        }

        return true;
    }
}
