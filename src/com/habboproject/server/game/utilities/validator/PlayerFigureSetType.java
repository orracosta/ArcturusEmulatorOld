package com.habboproject.server.game.utilities.validator;

import java.util.Map;

public class PlayerFigureSetType {

    private String typeName;
    private int paletteId;
    private Map<Integer, PlayerFigureSet> sets;

    public PlayerFigureSetType(final String typeName, final int paletteId, final Map<Integer, PlayerFigureSet> sets) {
        this.typeName = typeName;
        this.paletteId = paletteId;
        this.sets = sets;
    }

    public Map<Integer, PlayerFigureSet> getSets() {
        return this.sets;
    }

    public int getPaletteId() {
        return this.paletteId;
    }

    public String getTypeName() {
        return this.typeName;
    }
}
