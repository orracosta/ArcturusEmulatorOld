package com.eu.habbo.networking.camera.messages.incoming;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.networking.camera.CameraIncomingMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import javafx.scene.Camera;

public class CameraLoginStatusEvent extends CameraIncomingMessage
{
    public final static int LOGIN_OK = 0;
    public final static int LOGIN_ERROR = 1;
    public final static int NO_ACCOUNT = 2;
    public final static int ALREADY_LOGGED_IN = 3;
    public final static int BANNED = 4;
    public final static int OLD_BUILD = 5;

    public CameraLoginStatusEvent(Short header, ByteBuf body)
    {
        super(header, body);
    }

    @Override
    public void handle(Channel client) throws Exception
    {
        int status = this.readInt();

        if (status == LOGIN_ERROR)
        {
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Failed to login to Camera Server: Incorrect Details");
        }
        else if (status == NO_ACCOUNT)
        {
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Failed to login to Camera Server: No Account Found");
        }
        else if (status == BANNED)
        {
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Sorry but you seem to be banned from the Arcturus forums and therefor cant use the Camera Server :'(");
        }
        else if (status == ALREADY_LOGGED_IN)
        {
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] You seem to be already connected to the Camera Server");
        }
        else if (status == OLD_BUILD)
        {
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] This version of Arcturus Emulator is no longer supported by the Camera Server. Upgrade your emulator.");
        }

        if (status == LOGIN_OK)
        {
            CameraClient.isLoggedIn = true;
            System.out.println("[" + Logging.ANSI_GREEN + "CAMERA" + Logging.ANSI_RESET + "] Succesfully connected to the Arcturus Camera Server!");
        }
        else
        {
            CameraClient.attemptReconnect = false;
            Emulator.getCameraClient().disconnect();
        }
    }
}