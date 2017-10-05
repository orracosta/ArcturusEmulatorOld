package com.habboproject.server.network.messages.incoming.room.item;

import com.habboproject.server.game.rooms.objects.items.types.wall.MoodlightWallItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.items.MoodlightDao;


public class ToggleMoodlightMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }
        if (!room.getRights().hasRights(client.getPlayer().getEntity().getPlayerId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            client.disconnect();
            return;
        }

        MoodlightWallItem moodlight = room.getItems().getMoodlight();
        if (moodlight == null) {
            return;
        }

        if (!moodlight.getMoodlightData().isEnabled()) {
            moodlight.getMoodlightData().setEnabled(true);
        } else {
            moodlight.getMoodlightData().setEnabled(false);
        }

        // save the data!
        MoodlightDao.updateMoodlight(moodlight);

        // set the mood!
        moodlight.setExtraData(moodlight.generateExtraData());
        moodlight.saveData();
        moodlight.sendUpdate();
    }
}
