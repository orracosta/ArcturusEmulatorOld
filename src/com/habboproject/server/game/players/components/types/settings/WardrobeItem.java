package com.habboproject.server.game.players.components.types.settings;

import com.habboproject.server.api.game.players.data.types.IWardrobeItem;
import com.habboproject.server.utilities.JsonData;

public class WardrobeItem implements JsonData, IWardrobeItem {
    private int slot;
    private String gender;
    private String figure;

    public WardrobeItem(int slot, String gender, String figure) {
        this.slot = slot;
        this.gender = gender;
        this.figure = figure;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }
}
