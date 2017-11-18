package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolRoomInfoComposer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModToolRequestRoomInfoEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            int roomId = this.packet.readInt();

            if(roomId < 0)
                return;

            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

            if(room == null) {
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT rooms.* FROM room_enter_log INNER JOIN rooms ON room_enter_log.room_id = rooms.id WHERE rooms.id = ? LIMIT 1"))
                {
                    statement.setInt(1, roomId);

                    try (ResultSet set = statement.executeQuery())
                    {
                        while (set.next())
                        {
                            room = new Room(set);
                        }
                    }
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }

            if (room != null)
            {
                this.client.sendResponse(new ModToolRoomInfoComposer(room));
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.roominfo").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
