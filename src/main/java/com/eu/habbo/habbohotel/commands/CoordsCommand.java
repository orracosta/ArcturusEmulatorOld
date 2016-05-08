 package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;

/**
 * Created on 6-10-2014 20:37.
 */
public class CoordsCommand extends Command {

    public CoordsCommand()
    {
        super.permission = "cmd_coords";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_coords").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getRoomUnit() == null || gameClient.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return false;

        gameClient.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.generic.cmd_coords.title") + "\r\n" +
                "x: " + gameClient.getHabbo().getRoomUnit().getX() + "\r" +
                "y: " + gameClient.getHabbo().getRoomUnit().getY() + "\r" +
                "z: " + (gameClient.getHabbo().getRoomUnit().getStatus().containsKey("sit") ? gameClient.getHabbo().getRoomUnit().getStatus().get("sit") : gameClient.getHabbo().getRoomUnit().getZ()) + "\r" +
                Emulator.getTexts().getValue("generic.rotation.head") + ": " + gameClient.getHabbo().getRoomUnit().getHeadRotation() + "-" + gameClient.getHabbo().getRoomUnit().getHeadRotation().getValue() + "\r" +
                Emulator.getTexts().getValue("generic.rotation.body") + ": " + gameClient.getHabbo().getRoomUnit().getBodyRotation() + "-" + gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() + "\r" +
                Emulator.getTexts().getValue("generic.sitting") + ": " + (gameClient.getHabbo().getRoomUnit().getStatus().containsKey("sit") ? Emulator.getTexts().getValue("generic.yes") : Emulator.getTexts().getValue("generic.no"))));

        return true;
    }
}
