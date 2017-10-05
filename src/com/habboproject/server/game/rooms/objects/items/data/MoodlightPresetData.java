package com.habboproject.server.game.rooms.objects.items.data;

public class MoodlightPresetData {
    public boolean backgroundOnly;
    public String colour;
    public int intensity;

    public MoodlightPresetData(boolean backgroundOnly, String colour, int intensity) {
        this.backgroundOnly = backgroundOnly;
        this.colour = colour;
        this.intensity = intensity;
    }
}
