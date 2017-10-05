package com.habboproject.server.api.game.players.data;

public interface IPlayerData {

    void save();

    void decreaseCredits(int amount);

    void increaseCredits(int amount);

    void decreasePoints(int points);

    void increasePoints(int points);

    void increaseActivityPoints(int points);

    void decreaseActivityPoints(int points);

    void setPoints(int points);

    int getId();

    int getRank();

    String getUsername();

    void setUsername(String username);

    int getAchievementPoints();

    String getMotto();

    void setMotto(String motto);

    String getFigure();

    String getGender();

    int getCredits();

    void setCredits(int credits);

    int getVipPoints();

    int getLastVisit();

    String getRegDate();

    boolean isVip();

    void setVip(boolean vip);

    void setLastVisit(long time);

    void setFigure(String figure);

    void setGender(String gender);

    int getRegTimestamp();

    void setRegTimestamp(int regTimestamp);

    String getEmail();

    void setEmail(String email);

    int getFavouriteGroup();

    void setFavouriteGroup(int favouriteGroup);

    String getIpAddress();

    void setIpAddress(String ipAddress);

    int getActivityPoints();

    void setActivityPoints(int activityPoints);

    void setVipPoints(int vipPoints);

    void setRank(int rank);

    String getTemporaryFigure();

    void setTemporaryFigure(String temporaryFigure);

    int getQuestId();
}