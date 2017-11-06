package com.eu.habbo.messages.incoming.camera;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.camera.CameraPublishWaitMessageComposer;
import com.eu.habbo.messages.outgoing.camera.CameraRoomThumbnailSavedComposer;
import com.eu.habbo.messages.outgoing.camera.CameraURLComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.networking.camera.messages.outgoing.CameraRenderImageComposer;
import com.eu.habbo.util.crypto.ZIP;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import java.util.zip.Inflater;

public class CameraRoomPictureEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        if (!this.client.getHabbo().hasPermission("acc_camera")) {
            this.client.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("camera.permission")));
            return;
        }

        if (!this.client.getHabbo().getHabboInfo().getCurrentRoom().isOwner(this.client.getHabbo()))
            return;

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null)
            return;

        if (!room.isOwner(this.client.getHabbo()) && !this.client.getHabbo().hasPermission("acc_modtool_ticket_q"))
            return;

        final int count = this.packet.readInt();

        ByteBuf image = this.packet.getBuffer().readBytes(count);

        this.packet.readString();
        this.packet.readString();
        this.packet.readInt();
        this.packet.readInt();

        int timestamp = Emulator.getIntUnixTimestamp();

        String URL = room.getId() + "-" + this.client.getHabbo().getHabboInfo().getId() + "-" + timestamp;

        this.client.getHabbo().getHabboInfo().setPhotoTimestamp(timestamp);
        this.client.getHabbo().getHabboInfo().setPhotoRoomId(room.getId());
        this.client.getHabbo().getHabboInfo().setPhotoJSON(URL);

        BufferedImage theImage = ImageIO.read(new ByteBufInputStream(image));

        ImageIO.write(theImage, "png", new File("Z:\\swf\\assets\\camera\\" + URL + ".png"));

        this.client.sendResponse(new CameraURLComposer(URL));
    }
}
