package com.habboproject.server.game.commands.staff;

import com.google.common.collect.Lists;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.navigator.NavigatorManager;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.SqlHelper;
import com.habboproject.server.storage.queries.navigator.NavigatorDao;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class MakePublicCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || !room.getRights().hasRights(client.getPlayer().getId())) return;

        if(room.getData().getOwnerId() != 0) {
            room.getData().setOwner("Officialrooms");
            room.getData().setOwnerId(0);
            sendNotif("Este quarto agora é público!", client);

            Connection sqlConnection = null;
            PreparedStatement preparedStatement = null;

            try {

                int order = NavigatorDao.getPublicRooms().size()+1;

                sqlConnection = SqlHelper.getConnection();
                preparedStatement = SqlHelper.prepare("INSERT INTO navigator_publics (`room_id`, `caption`, `description`, `order_num`, `image_url`, `enabled`) VALUES (?, ?, ?, ?, '', '1')",
                        sqlConnection);

                preparedStatement.setInt(1, room.getId());
                preparedStatement.setString(2, room.getData().getName());
                preparedStatement.setString(3, room.getData().getDescription());
                preparedStatement.setInt(4, order);

                preparedStatement.execute();
            } catch (SQLException e) {
                Comet.getServer().getLogger().error("Failed to make public room");
                SqlHelper.handleSqlException(e);
            } finally {
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }

        }
        else {

            room.getData().setOwner(client.getPlayer().getData().getUsername());
            room.getData().setOwnerId(client.getPlayer().getData().getId());
            sendNotif("Este quarto não é mais público e foi adicionado aos seus quartos!", client);
            Connection sqlConnection = null;
            PreparedStatement preparedStatement = null;

            try {

                int order = NavigatorDao.getPublicRooms().size()+1;

                sqlConnection = SqlHelper.getConnection();
                preparedStatement = SqlHelper.prepare("DELETE FROM navigator_publics WHERE room_id = ?",
                        sqlConnection);

                preparedStatement.setInt(1, room.getId());

                preparedStatement.execute();
            } catch (SQLException e) {
                Comet.getServer().getLogger().error("Failed to remove public room");
                SqlHelper.handleSqlException(e);
            } finally {
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        }

        NavigatorManager.getInstance().loadCategories();
        NavigatorManager.getInstance().loadPublicRooms();
        NavigatorManager.getInstance().loadStaffPicks();

        int roomId = room.getId();

        List<Player> players = Lists.newArrayList();
        for (PlayerEntity entity : room.getEntities().getPlayerEntities()) {
            players.add(entity.getPlayer());
        }

        room.getData().save();
        room.getItems().commit();

        RoomManager.getInstance().forceUnload(roomId);

        ThreadManager.getInstance().execute(new CometThread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }

                for (Player player : players) {
                    if (player == null || player.getSession() == null)
                        continue;

                    player.getSession().send(new RoomForwardMessageComposer(roomId));
                }
            }
        });

    }

    @Override
    public String getPermission() {
        return "makepublic_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.makepublic.description");
    }
}