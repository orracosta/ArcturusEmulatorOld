package com.eu.habbo.networking.camera.messages.incoming;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.networking.camera.CameraIncomingMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class CameraLoginStatusEvent extends CameraIncomingMessage
{
    public final static int LOGIN_OK = 0;
    public final static int LOGIN_ERROR = 1;
    public final static int NO_ACCOUNT = 2;
    public final static int ALREADY_LOGGED_IN = 3;
    public final static int BANNED = 4;
    public final static int OLD_BUILD = 5;
    public final static int NO_CAMERA_SUBSCRIPTION = 6;

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
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Failed to login to Camera Server: No Account Found. Register for free on the Arcturus Forums! Visit http://arcturus.wf/");
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
        else if (status == NO_CAMERA_SUBSCRIPTION)
        {
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] You don't have a Camera Subscription and therefor cannot use the camera!");
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Please consider making a donation to keep this project going. The emulator can be used free of charge!");
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] A trial version is available for $2.5. A year subscription is only $10 and a permanent subscription is $25.");
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] By donating this subscription you support the development of the emulator you are using :)");
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Visit http://arcturus.wf/mysubscriptions.php to buy your subscription!");
            System.out.println("");
            System.out.println("\t\t\t\t Please Consider getting a subscription. Regards: The General");
        }

        if (status == LOGIN_OK)
        {
            CameraClient.isLoggedIn = true;
            System.out.println("[" + Logging.ANSI_GREEN + "CAMERA" + Logging.ANSI_RESET + "] Succesfully connected to the Arcturus Camera Server!");
        }
        else
        {
            CameraClient.attemptReconnect = false;
        }
    }
}