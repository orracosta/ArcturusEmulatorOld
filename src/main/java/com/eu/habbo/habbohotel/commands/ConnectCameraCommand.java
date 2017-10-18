package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.threading.runnables.CameraClientAutoReconnect;

public class ConnectCameraCommand extends Command
{
    public ConnectCameraCommand()
    {
        super("cmd_connect_camera", Emulator.getTexts().getValue("commands.keys.cmd_connect_camera").split(";"));
    }

        @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        CameraClient.attemptReconnect = true;
        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_alert.cmd_connect_camera"), RoomChatMessageBubbles.ALERT);
        return true;
    }
}