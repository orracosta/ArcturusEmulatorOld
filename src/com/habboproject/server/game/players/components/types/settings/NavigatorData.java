package com.habboproject.server.game.players.components.types.settings;

import com.habboproject.server.utilities.JsonData;

/**
 * Created by brend on 05/02/2017.
 */
public class NavigatorData implements JsonData {
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean showSavedSearches;

    public NavigatorData(int x, int y, int width, int height, boolean showSavedSearches) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.showSavedSearches = showSavedSearches;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean showSavedSearches() {
        return this.showSavedSearches;
    }

    public void setShowSavedSearches(boolean showSavedSearches) {
        this.showSavedSearches = showSavedSearches;
    }
}
