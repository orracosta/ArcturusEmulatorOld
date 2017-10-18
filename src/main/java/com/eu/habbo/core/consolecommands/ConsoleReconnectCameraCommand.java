package com.eu.habbo.core.consolecommands;

import com.eu.habbo.networking.camera.CameraClient;

public class ConsoleReconnectCameraCommand extends ConsoleCommand
{
    public ConsoleReconnectCameraCommand()
    {
        super("camera", "");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        System.out.println("Connecting to the camera...");
        CameraClient.attemptReconnect = true;
    }
}