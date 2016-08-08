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
            System.out.println("[" + Logging.ANSI_YELLOW + "CAMERA" + Logging.ANSI_RESET + "] Attempting to connect to the Camera server.");
            if (Emulator.getCameraClient() != null)
            {
                Emulator.getCameraClient().disconnect();
            }

            try
            {
                Emulator.setCameraClient(new CameraClient());
                Emulator.getCameraClient().connect();
            }
            catch (Exception e)
            {
                System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Failed to start the camera client.");
            }

            if (CameraClient.channel == null)
            {
                Emulator.getThreading().run(this, 5000);
            }
        }
    }
}