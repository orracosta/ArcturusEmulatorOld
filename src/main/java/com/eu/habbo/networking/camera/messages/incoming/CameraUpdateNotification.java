package com.eu.habbo.networking.camera.messages.incoming;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.networking.camera.CameraIncomingMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class CameraUpdateNotification extends CameraIncomingMessage
{
    public CameraUpdateNotification(Short header, ByteBuf body)
    {
        super(header, body);
    }

    @Override
    public void handle(Channel client) throws Exception
    {
        boolean alert = this.readBoolean();
        String message = this.readString();
        int type = this.readInt();

        if (type == 0)
        {
            System.out.println("[" + Logging.ANSI_GREEN + "CAMERA" + Logging.ANSI_RESET + "] " + message);
        }
        else if (type == 1)
        {
            System.out.println("[" + Logging.ANSI_YELLOW + "CAMERA" + Logging.ANSI_RESET + "] " + message);
        }
        else if (type == 2)
        {
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] " + message);
        }

        if (alert)
        {
            Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new GenericAlertComposer(message).compose());
        }
    }
}