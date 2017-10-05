package com.habboproject.server.game.players.types;

import com.habboproject.server.api.game.players.data.IPlayerStatistics;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.storage.queries.player.messenger.MessengerDao;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PlayerStatistics implements IPlayerStatistics {
    private int playerId;
    private int achievementPoints;

    private int dailyRespects;
    private int respectPoints;

    private int scratches;

    private int helpTickets;
    private int abusiveHelpTickets;
    private int cautions;
    private int bans;

    private String lastTotemEffect;

    public PlayerStatistics(ResultSet data, boolean isLogin) throws SQLException {
        if (isLogin) {
            this.playerId = data.getInt("playerId");
            this.achievementPoints = data.getInt("playerStats_achievementPoints");
            this.dailyRespects = data.getInt("playerStats_dailyRespects");
            this.respectPoints = data.getInt("playerStats_totalRespectPoints");
            this.helpTickets = data.getInt("playerStats_helpTickets");
            this.abusiveHelpTickets = data.getInt("playerStats_helpTicketsAbusive");
            this.cautions = data.getInt("playerStats_cautions");
            this.bans = data.getInt("playerStats_bans");
            this.scratches = data.getInt("playerStats_scratches");
            this.lastTotemEffect = data.getString("playerStats_lastTotemEffect");
        } else {
            this.playerId = data.getInt("player_id");
            this.achievementPoints = data.getInt("achievement_score");
            this.dailyRespects = data.getInt("daily_respects");
            this.respectPoints = data.getInt("total_respect_points");
            this.helpTickets = data.getInt("help_tickets");
            this.abusiveHelpTickets = data.getInt("help_tickets_abusive");
            this.cautions = data.getInt("cautions");
            this.bans = data.getInt("bans");
            this.scratches = data.getInt("daily_scratches");
            this.lastTotemEffect = data.getString("last_totem_effect");
        }
    }

    public PlayerStatistics(int userId) {
        this.playerId = userId;
        this.achievementPoints = 0;
        this.respectPoints = 0;
        this.dailyRespects = 3;
        this.scratches = 3;
        this.helpTickets = 0;
        this.abusiveHelpTickets = 0;
        this.cautions = 0;
        this.bans = 0;
        this.lastTotemEffect = "";
    }

    public void save() {
        PlayerDao.updatePlayerStatistics(this);
    }

    public void incrementAchievementPoints(int amount) {
        this.achievementPoints += amount;
        this.save();
    }

    public void incrementCautions(int amount) {
        this.cautions += amount;
        this.save();
    }

    public void incrementRespectPoints(int amount) {
        this.respectPoints += amount;
        this.save();
    }

    public void decrementDailyRespects(int amount) {
        this.dailyRespects -= amount;
        this.save();
    }

    public void incrementBans(int amount) {
        this.bans += amount;
    }

    public void incrementAbusiveHelpTickets(int amount) {
        this.abusiveHelpTickets += amount;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getDailyRespects() {
        return this.dailyRespects;
    }

    public int getRespectPoints() {
        return this.respectPoints;
    }

    public int getAchievementPoints() {
        return this.achievementPoints;
    }

    public int getFriendCount() {
        return MessengerDao.getFriendCount(this.playerId);
    }

    public int getHelpTickets() {
        return helpTickets;
    }

    public void setHelpTickets(int helpTickets) {
        this.helpTickets = helpTickets;
    }

    public int getAbusiveHelpTickets() {
        return abusiveHelpTickets;
    }

    public void setAbusiveHelpTickets(int abusiveHelpTickets) {
        this.abusiveHelpTickets = abusiveHelpTickets;
    }

    public int getCautions() {
        return cautions;
    }

    public void setCautions(int cautions) {
        this.cautions = cautions;
    }

    public int getBans() {
        return bans;
    }

    public void setBans(int bans) {
        this.bans = bans;
    }

    @Override
    public void setDailyRespects(int points) {
        this.dailyRespects = points;
    }

    @Override
    public void setScratches(int scratches) {
        this.scratches = scratches;
    }

    @Override
    public int getScratches() {
        return scratches;
    }

    public String getLastTotemEffect() {
        return lastTotemEffect;
    }

    public void setLastTotemEffect(String lastTotemEffect) {
        this.lastTotemEffect = lastTotemEffect;
    }
}
