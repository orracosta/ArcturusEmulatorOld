package com.habboproject.server.api.game.players.data.types;

public interface IWardrobeItem {
    public int getSlot();

    public void setSlot(int slot);

    public String getGender();

    public void setGender(String gender);

    public String getFigure();

    public void setFigure(String figure);
}
