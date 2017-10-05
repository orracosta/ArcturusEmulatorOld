package com.habboproject.server.network.messages.incoming.user.wardrobe;

import com.habboproject.server.api.game.players.data.types.IWardrobeItem;
import com.habboproject.server.game.players.components.types.settings.WardrobeItem;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.utilities.JsonFactory;

import java.util.List;


public class SaveWardrobeMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int slot = msg.readInt();
        String figure = msg.readString();
        String gender = msg.readString();

        List<IWardrobeItem> wardrobe = client.getPlayer().getSettings().getWardrobe();

        boolean wardrobeUpdated = false;

        for (IWardrobeItem item : wardrobe) {
            if (item.getSlot() == slot) {
                item.setFigure(figure);
                item.setGender(gender);

                wardrobeUpdated = true;
            }
        }

        if (!wardrobeUpdated) {
            wardrobe.add(new WardrobeItem(slot, gender, figure));
        }

        client.getPlayer().getSettings().setWardrobe(wardrobe);
        PlayerDao.saveWardrobe(JsonFactory.getInstance().toJson(wardrobe), client.getPlayer().getId());
    }
}
