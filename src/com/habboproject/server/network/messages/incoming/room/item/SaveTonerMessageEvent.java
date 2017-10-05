package com.habboproject.server.network.messages.incoming.room.item;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.data.BackgroundTonerData;
import com.habboproject.server.game.rooms.objects.items.types.floor.toner.BackgroundTonerFloorItem;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class SaveTonerMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int tonerId = msg.readInt();

        int hue = msg.readInt();
        int saturation = msg.readInt();
        int lightness = msg.readInt();

        RoomItemFloor item = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(tonerId);

        if (item == null || !client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl() || !(item instanceof BackgroundTonerFloorItem)) {
            return;
        }

        BackgroundTonerData data = new BackgroundTonerData(hue, saturation, lightness);
        String stringData = BackgroundTonerData.get(data);

        item.setExtraData(stringData);

        item.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(item));
        item.saveData();
    }
}
