package com.habboproject.server.game.utilities.validator;

public class PlayerFigureSet {
    private String gender;
    private int clubCode;
    private boolean colorable;
    private boolean selectable;
    private int colorCount;

    public PlayerFigureSet(final String gender, final int clubCode, final boolean colorable, final boolean selectable, final int colorCount) {
        this.gender = gender;
        this.clubCode = clubCode;
        this.colorable = colorable;
        this.selectable = selectable;
        this.colorCount = colorCount;
    }

    public String getGender() {
        return this.gender;
    }

    public int getClubCode() {
        return this.clubCode;
    }

    public boolean isColorable() {
        return this.colorable;
    }

    public boolean isSelectable() {
        return this.selectable;
    }

    public int getColorCount() {
        return this.colorCount;
    }
}

