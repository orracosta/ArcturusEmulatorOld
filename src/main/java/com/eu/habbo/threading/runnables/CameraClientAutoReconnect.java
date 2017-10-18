package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.networking.camera.CameraClient;

public class CameraClientAutoReconnect implements Runnable
{
    @Override
    public void run()
    {
        if (CameraClient.attemptReconnect && !Emulator.isShuttingDown)
        {
            if (!(CameraClient.channelFuture != null && CameraClient.channelFuture.channel().isRegistered()))
            {
                System.out.println("[" + Logging.ANSI_YELLOW + "CAMERA" + Logging.ANSI_RESET + "] Attempting to connect to the Camera server.");
                if (Emulator.getCameraClient() != null)
                {
                    Emulator.getCameraClient().disconnect();
                }
                else
                {
                    Emulator.setCameraClient(new CameraClient());
                }

                try
                {
                    Emulator.getCameraClient().connect();
                }
                catch (Exception e)
                {
                    System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Failed to start the camera client.");
                }
            }
            else
            {
                CameraClient.attemptReconnect = false;
                System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Already connected to the camera. Reconnecting not needed!");
            }
        }

        Emulator.getThreading().run(this, 5000);
    }
}