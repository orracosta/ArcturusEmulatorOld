package com.eu.habbo.messages.incoming.camera;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.camera.CameraPublishWaitMessageComposer;
import com.eu.habbo.messages.outgoing.camera.CameraPurchaseSuccesfullComposer;
import com.eu.habbo.plugin.events.users.UserPublishPictureEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CameraPublishToWebEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (this.client.getHabbo().getHabboInfo().getPhotoTimestamp() != 0)
        {
            if (!this.client.getHabbo().getHabboInfo().getPhotoJSON().isEmpty())
            {
                if (this.client.getHabbo().getHabboInfo().getPhotoJSON().contains(this.client.getHabbo().getHabboInfo().getPhotoTimestamp() + ""))
                {
                    int timestamp = Emulator.getIntUnixTimestamp();

                    boolean published = false;
                    int timeDiff = timestamp - this.client.getHabbo().getHabboInfo().getWebPublishTimestamp();
                    int wait = 0;
                    if (timeDiff < Emulator.getConfig().getInt("camera.publish.delay"))
                    {
                        wait = timeDiff - Emulator.getConfig().getInt("camera.publish.delay");
                    }
                    else
                    {
                        UserPublishPictureEvent publishPictureEvent = new UserPublishPictureEvent(this.client.getHabbo(), this.client.getHabbo().getHabboInfo().getPhotoURL(), timestamp, this.client.getHabbo().getHabboInfo().getPhotoRoomId());
                        if (!Emulator.getPluginManager().fireEvent(publishPictureEvent).isCancelled())
                        {
                            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO camera_web (user_id, room_id, timestamp, url) VALUES (?, ?, ?, ?)"))
                            {
                                statement.setInt(1, this.client.getHabbo().getHabboInfo().getId());
                                statement.setInt(2, publishPictureEvent.roomId);
                                statement.setInt(3, publishPictureEvent.timestamp);
                                statement.setString(4, publishPictureEvent.URL);
                                statement.execute();
                                this.client.getHabbo().getHabboInfo().setWebPublishTimestamp(timestamp);
                                published = true;
                            }
                            catch (SQLException e)
                            {
                                Emulator.getLogging().logSQLException(e);
                            }
                        }
                        else
                        {
                            return;
                        }
                    }

                    this.client.sendResponse(new CameraPublishWaitMessageComposer(published, wait, published ? this.client.getHabbo().getHabboInfo().getPhotoURL() : ""));
                }
            }
        }
    }
}