package com.eu.habbo.messages.incoming.camera;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.camera.CameraPublishWaitMessageComposer;
import com.eu.habbo.messages.outgoing.camera.CameraURLComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.networking.camera.messages.outgoing.CameraRenderImageComposer;
import com.eu.habbo.util.crypto.ZIP;

import java.io.ByteArrayOutputStream;
import java.util.zip.Inflater;

public class CameraRoomPictureEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (CameraClient.isLoggedIn)
        {
            int seconds = Emulator.getIntUnixTimestamp() - this.client.getHabbo().getHabboInfo().getPhotoTimestamp();
            if (seconds < (60 * 2))
            {
                this.client.sendResponse(new CameraPublishWaitMessageComposer(false, seconds - (60 * 2), ""));
            }

            System.out.println(this.packet.getBuffer().readFloat());
            //byte[] buffer = new byte[4096*3];
            byte[] data = this.packet.getBuffer().readBytes(this.packet.getBuffer().readableBytes()).array();

        /*Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();

        inflater.end();*/
            String content = new String(ZIP.inflate(data));

            CameraRenderImageComposer composer = new CameraRenderImageComposer(this.client.getHabbo().getHabboInfo().getId(), this.client.getHabbo().getHabboInfo().getCurrentRoom().getBackgroundTonerColor().getRGB(), 320, 320, content);

            this.client.getHabbo().getHabboInfo().setPhotoJSON(Emulator.getConfig().getValue("camera.extradata").replace("%timestamp%", composer.timestamp + ""));
            this.client.getHabbo().getHabboInfo().setPhotoTimestamp(composer.timestamp);

            Emulator.getCameraClient().sendMessage(composer);

            //Emulator.getCameraClient().sendMessage(new CameraRenderImageComposer(this.client.getHabbo().getHabboInfo().getId(), this.client.getHabbo().getHabboInfo().getCurrentRoom().getBackgroundTonerColor().getRGB(), 320, 320, ""));
        }
        else
        {
            this.client.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("camera.disabled")));
        }

    }
}
