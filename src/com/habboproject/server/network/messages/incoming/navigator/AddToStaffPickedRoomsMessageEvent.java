package com.habboproject.server.network.messages.incoming.navigator;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.navigator.NavigatorManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomDataMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.navigator.NavigatorDao;

public class AddToStaffPickedRoomsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (!client.getPlayer().getPermissions().getRank().roomStaffPick()) {
            return;
        }

        if (client.getPlayer().getEntity() == null)
            return;

        Room room = client.getPlayer().getEntity().getRoom();

        if (NavigatorManager.getInstance().isStaffPicked(room.getId())) {
            NavigatorDao.deleteStaffPick(room.getId());

            NavigatorManager.getInstance().getStaffPicks().remove(room.getId());
            client.send(new AdvancedAlertMessageComposer(Locale.get("navigator.staff.picks.removed.title"), Locale.get("navigator.staff.picks.removed.message")));
        } else {

            NavigatorManager.getInstance().getStaffPicks().add(room.getId());
            NavigatorDao.staffPick(room.getId(), client.getPlayer().getData().getId());

            client.send(new AdvancedAlertMessageComposer(Locale.get("navigator.staff.picks.added.title"), Locale.get("navigator.staff.picks.added.message")));
        }

        room.getEntities().broadcastMessage(new RoomDataMessageComposer(room));
    }
}
