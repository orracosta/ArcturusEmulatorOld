package com.habboproject.server.network.messages.incoming.navigator;

import com.habboproject.server.game.players.components.types.settings.NavigatorData;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.utilities.JsonFactory;

/**
 * Created by brend on 31/01/2017.
 */
public class ResizeNavigatorMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer() == null) {
            return;
        }
        int x = msg.readInt();
        int y = msg.readInt();
        int width = msg.readInt();
        int height = msg.readInt();

        if (client.getPlayer().getSettings().getNavigator().getX() == x && client.getPlayer().getSettings().getNavigator().getY() == y && client.getPlayer().getSettings().getNavigator().getWidth() == width && client.getPlayer().getSettings().getNavigator().getHeight() == height) {
            return;
        }

        PlayerDao.saveNavigatorSize(JsonFactory.getInstance().toJson(new NavigatorData(x, y, width, height, false)), client.getPlayer().getId());
    }
}