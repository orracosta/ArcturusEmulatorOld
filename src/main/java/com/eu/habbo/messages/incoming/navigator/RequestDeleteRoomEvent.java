package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.events.navigator.NavigatorRoomDeletedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RequestDeleteRoomEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int roomId = this.packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

        if(room != null)
        {
            if (room.isOwner(this.client.getHabbo()))
            {
                if(Emulator.getPluginManager().fireEvent(new NavigatorRoomDeletedEvent(this.client.getHabbo(), room)).isCancelled())
                {
                    return;
                }

                room.ejectAll();
                room.ejectUserFurni(room.getOwnerId());

                if(room.getGuildId() > 0)
                {
                    Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(room.getGuildId());

                    if(guild != null)
                    {
                        Emulator.getGameEnvironment().getGuildManager().deleteGuild(guild);
                    }
                }

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
                {
                    try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rooms WHERE id = ? LIMIT 1"))
                    {
                        statement.setInt(1, roomId);
                        statement.execute();
                    }

                    room.preventUnloading = false;

                    if (room.hasCustomLayout())
                    {
                        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM room_models_custom WHERE id = ? LIMIT 1"))
                        {
                            stmt.setInt(1, roomId);
                            stmt.execute();
                        }
                    }

                    Emulator.getGameEnvironment().getRoomManager().unloadRoom(room);

                    try (PreparedStatement rights = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ?"))
                    {
                        rights.setInt(1, roomId);
                        rights.execute();
                    }

                    try (PreparedStatement votes = connection.prepareStatement("DELETE FROM room_votes WHERE room_id = ?"))
                    {
                        votes.setInt(1, roomId);
                        votes.execute();
                    }

                    try (PreparedStatement filter = connection.prepareStatement("DELETE FROM room_wordfilter WHERE room_id = ?"))
                    {
                        filter.setInt(1, roomId);
                        filter.execute();
                    }

                    //Guerejo FIX
                    Emulator.getGameEnvironment().getRoomManager().unloadRoomsForHabbo(this.client.getHabbo());
                    Emulator.getGameEnvironment().getRoomManager().loadRoomsForHabbo(this.client.getHabbo());
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }
            else
            {
                String message = Emulator.getTexts().getValue("scripter.warning.room.delete").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%roomname%", room.getName()).replace("%roomowner%", room.getOwnerName());
                Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", message);
                Emulator.getLogging().logUserLine(message);
            }
        }
    }
}
