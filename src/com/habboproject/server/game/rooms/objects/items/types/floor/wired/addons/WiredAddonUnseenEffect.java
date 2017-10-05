package com.habboproject.server.game.rooms.objects.items.types.floor.wired.addons;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.google.common.collect.Lists;

import java.util.List;


public class WiredAddonUnseenEffect extends RoomItemFloor {
    private List<Long> seenEffects;

    public WiredAddonUnseenEffect(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        this.seenEffects = Lists.newArrayList();
    }

    public List<Long> getSeenEffects() {
        return seenEffects;
    }
}
