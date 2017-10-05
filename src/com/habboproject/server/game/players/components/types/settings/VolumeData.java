package com.habboproject.server.game.players.components.types.settings;

import com.habboproject.server.api.game.players.data.types.IVolumeData;
import com.habboproject.server.utilities.JsonData;

public class VolumeData implements JsonData, IVolumeData {
    private int systemVolume;
    private int furniVolume;
    private int traxVolume;

    public VolumeData(int systemVolume, int furniVolume, int traxVolume) {
        this.systemVolume = systemVolume;
        this.furniVolume = furniVolume;
        this.traxVolume = traxVolume;
    }

    public int getSystemVolume() {
        return systemVolume;
    }

    public void setSystemVolume(int systemVolume) {
        this.systemVolume = systemVolume;
    }

    public int getFurniVolume() {
        return furniVolume;
    }

    public void setFurniVolume(int furniVolume) {
        this.furniVolume = furniVolume;
    }

    public int getTraxVolume() {
        return traxVolume;
    }

    public void setTraxVolume(int traxVolume) {
        this.traxVolume = traxVolume;
    }
}
