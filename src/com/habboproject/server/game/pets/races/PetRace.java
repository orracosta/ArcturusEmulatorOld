package com.habboproject.server.game.pets.races;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PetRace {
    private int raceId;
    private int colour1;
    private int colour2;

    private boolean hasColour1;
    private boolean hasColour2;

    public PetRace(ResultSet data) throws SQLException {
        this.raceId = data.getInt("race_id");

        this.colour1 = data.getInt("colour1");
        this.colour2 = data.getInt("colour2");

        this.hasColour1 = data.getString("has1colour").equals("1");
        this.hasColour2 = data.getString("has2colour").equals("1");
    }

    public int getRaceId() {
        return raceId;
    }

    public int getColour1() {
        return colour1;
    }

    public int getColour2() {
        return colour2;
    }

    public boolean hasColour1() {
        return hasColour1;
    }

    public boolean hasColour2() {
        return hasColour2;
    }
}