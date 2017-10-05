package com.habboproject.server.network.messages.incoming.room.item.lovelock;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.valentines.LoveLockFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.lovelock.LoveLockCloseWidgetMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.lovelock.LoveLockConfirmedMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConfirmLoveLockMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }

        final int itemId = msg.readInt();
        final boolean confirmed = msg.readBoolean();

        RoomItemFloor floorItem = room.getItems().getFloorItem(itemId);

        if (floorItem == null || !(floorItem instanceof LoveLockFloorItem)) return;

        final int leftEntity = ((LoveLockFloorItem) floorItem).getLeftEntity();
        final int rightEntity = ((LoveLockFloorItem) floorItem).getRightEntity();

        if (leftEntity == 0 || rightEntity == 0) return;

        PlayerEntity leftPlayer = room.getEntities().getEntityByPlayerId(leftEntity);
        PlayerEntity rightPlayer = room.getEntities().getEntityByPlayerId(rightEntity);

        if (leftPlayer == null || rightPlayer == null) return;

        client.send(new LoveLockConfirmedMessageComposer(floorItem.getVirtualId()));

        if (confirmed) {
            boolean bothConfirmed = false;

            if (leftEntity == client.getPlayer().getId()) {
                leftPlayer.setAttribute("lovelockConfirm", true);

                if (rightPlayer.hasAttribute("lovelockConfirm")) {
                    bothConfirmed = true;
                }
            } else {
                rightPlayer.setAttribute("lovelockConfirm", true);

                if (leftPlayer.hasAttribute("lovelockConfirm")) {
                    bothConfirmed = true;
                }
            }

            if (bothConfirmed) {
                leftPlayer.removeAttribute("lovelockConfirm");
                rightPlayer.removeAttribute("lovelockConfirm");

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final String date = dateFormat.format(Calendar.getInstance().getTime());

                final String itemData = "1" + (char) 5 + leftPlayer.getUsername() + (char) 5 + rightPlayer.getUsername() + (char) 5 + leftPlayer.getFigure() + (char) 5 + rightPlayer.getFigure() + (char) 5 + date;

                floorItem.setExtraData(itemData);

                room.getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(floorItem));
                floorItem.saveData();

                leftPlayer.getPlayer().getSession().send(new LoveLockCloseWidgetMessageComposer(floorItem.getVirtualId()));
                rightPlayer.getPlayer().getSession().send(new LoveLockCloseWidgetMessageComposer(floorItem.getVirtualId()));
            }
        }
    }
}
