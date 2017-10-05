package com.habboproject.server.game.rooms.objects.entities.types.data.types;

import com.habboproject.server.game.rooms.objects.entities.types.data.BotDataObject;

import java.util.List;

public class SpyBotData implements BotDataObject {
    private List<String> visitors;

    public SpyBotData(List<String> visitors) {
        this.visitors = visitors;
    }

    public List<String> getVisitors() {
        return visitors;
    }
}
