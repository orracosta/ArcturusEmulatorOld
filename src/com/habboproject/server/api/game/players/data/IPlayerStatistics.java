package com.habboproject.server.api.game.players.data;

public interface IPlayerStatistics {
    void save();

    void incrementAchievementPoints(int amount);

    void incrementCautions(int amount);

    void incrementRespectPoints(int amount);

    void decrementDailyRespects(int amount);

    void incrementBans(int amount);

    void incrementAbusiveHelpTickets(int amount);

    int getPlayerId();

    int getDailyRespects();

    int getRespectPoints();

    int getAchievementPoints();

    int getFriendCount();

    int getHelpTickets();

    void setHelpTickets(int helpTickets);

    int getAbusiveHelpTickets();

    void setAbusiveHelpTickets(int abusiveHelpTickets);

    int getCautions();

    void setCautions(int cautions);

    int getBans();

    void setBans(int bans);

    void setDailyRespects(int points);

    void setScratches(int scratches);

    int getScratches();
}
