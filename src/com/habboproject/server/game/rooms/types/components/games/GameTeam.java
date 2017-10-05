package com.habboproject.server.game.rooms.types.components.games;

import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffectType;


public enum GameTeam {
    NONE(0),
    RED(1),
    GREEN(2),
    BLUE(3),
    YELLOW(4);

    private final int teamId;

    GameTeam(int team) {
        this.teamId = team;
    }

    public int getTeamId() {
        return this.teamId;
    }

    public int getBanzaiEffect() {
        switch (teamId) {
            case 1:
                return PlayerEffectType.BB_RED.getEffectId();

            case 2:
                return PlayerEffectType.BB_GREEN.getEffectId();

            case 3:
                return PlayerEffectType.BB_BLUE.getEffectId();

            case 4:
                return PlayerEffectType.BB_YELLOW.getEffectId();
        }

        return 0;
    }

    public int getFreezeEffect() {
        switch (teamId) {
            case 1:
                return PlayerEffectType.ES_RED.getEffectId();

            case 2:
                return PlayerEffectType.ES_GREEN.getEffectId();

            case 3:
                return PlayerEffectType.ES_BLUE.getEffectId();

            case 4:
                return PlayerEffectType.ES_YELLOW.getEffectId();
        }

        return 0;
    }
}
