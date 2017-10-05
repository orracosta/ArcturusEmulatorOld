package com.habboproject.server.game.rooms.objects.items.types.floor.others;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.google.common.collect.Lists;

import java.util.List;

public class PrivateChatFloorItem extends DefaultFloorItem {

    private List<PlayerEntity> entities = Lists.newArrayList();

    public PrivateChatFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public void onEntityStepOn(RoomEntity entity) {
        if(!(entity instanceof PlayerEntity) || this.entities.contains(entity)) return;

        entity.setPrivateChatItemId(this.getId());
        this.entities.add((PlayerEntity) entity);
    }

    @Override
    public void onEntityStepOff(RoomEntity entity) {
        if(!(entity instanceof PlayerEntity)) return;

        entity.setPrivateChatItemId(0);
        this.entities.remove(entity);
    }

    public void broadcastMessage(MessageComposer msg) {
        for(PlayerEntity playerEntity : this.entities) {
            playerEntity.getPlayer().getSession().send(msg);
        }
    }
}
