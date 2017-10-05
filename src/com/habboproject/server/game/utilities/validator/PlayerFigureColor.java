package com.habboproject.server.game.utilities.validator;

public class PlayerFigureColor {
    private int clubCode;
    private boolean selectable;

    public PlayerFigureColor(final int clubCode, final boolean selectable) {
        this.clubCode = clubCode;
        this.selectable = selectable;
    }

    public int getClubCode() {
        return this.clubCode;
    }

    public boolean isSelectable() {
        return this.selectable;
    }

}

