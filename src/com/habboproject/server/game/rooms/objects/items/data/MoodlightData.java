package com.habboproject.server.game.rooms.objects.items.data;

import java.util.List;


public class MoodlightData {
    private boolean enabled;
    private int activePreset;
    private List<MoodlightPresetData> presets;

    public MoodlightData(boolean enabled, int activePreset, List<MoodlightPresetData> presets) {
        this.enabled = enabled;
        this.activePreset = activePreset;
        this.presets = presets;
    }

    public void updatePreset(int presetIndex, boolean bgOnly, String color, int intensity) {
        if (presets.get(presetIndex) == null) {
            return;
        }
        MoodlightPresetData data = presets.get(presetIndex);

        data.backgroundOnly = bgOnly;
        data.colour = color;
        data.intensity = intensity;
    }

    public List<MoodlightPresetData> getPresets() {
        return presets;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getActivePreset() {
        return activePreset;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setActivePreset(int activePreset) {
        this.activePreset = activePreset;
    }
}
