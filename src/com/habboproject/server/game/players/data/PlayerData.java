package com.habboproject.server.game.players.data;

import com.habboproject.server.api.game.players.data.IPlayerData;
import com.habboproject.server.game.utilities.validator.PlayerFigureValidator;
import com.habboproject.server.storage.SqlHelper;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PlayerData implements PlayerAvatar, IPlayerData, CometThread {
    public static final String DEFAULT_FIGURE = "hr-100-61.hd-180-2.sh-290-91.ch-210-66.lg-270-82";

    private int id;
    private int rank;

    private String username;
    private String motto;
    private String figure;
    private String gender;
    private String email;

    private String ipAddress;

    private int credits;
    private int vipPoints;
    private int activityPoints;

    private String regDate;
    private int lastVisit;
    private int regTimestamp;
    private int achievementPoints;

    private int favouriteGroup;

    private String temporaryFigure;

    private boolean vip;
    private int questId;

    private int forumPosts;

    private String newbieStep;

    public PlayerData(int id, String username, String motto, String figure, String gender, String email, int rank,
                      int credits, int vipPoints, int activityPoints, String reg, int lastVisit, boolean vip,
                      int achievementPoints, int regTimestamp, int favouriteGroup, String ipAddress,
                      int questId, int forumPosts, String newbieStep) {
        this.id = id;
        this.username = username;
        this.motto = motto;
        this.figure = figure;
        this.rank = rank;
        this.credits = credits;
        this.vipPoints = vipPoints;
        this.activityPoints = activityPoints;
        this.gender = gender;
        this.vip = vip;
        this.achievementPoints = achievementPoints;
        this.email = email;
        this.regDate = reg;
        this.lastVisit = lastVisit;
        this.regTimestamp = regTimestamp;
        this.favouriteGroup = favouriteGroup;
        this.ipAddress = ipAddress;
        this.questId = questId;
        this.forumPosts = forumPosts;
        this.newbieStep = newbieStep;

        if(this.figure != null) {
            if (!PlayerFigureValidator.isValidFigureCode(this.figure, this.gender.toLowerCase())) {
                this.figure = DEFAULT_FIGURE;
            }
        }
    }

    public PlayerData(ResultSet data) throws SQLException {
        this(data.getInt("playerId"),
                data.getString("playerData_username"),
                data.getString("playerData_motto"),
                data.getString("playerData_figure"),
                data.getString("playerData_gender"),
                data.getString("playerData_email"),
                data.getInt("playerData_rank"),
                data.getInt("playerData_credits"),
                data.getInt("playerData_vipPoints"),
                data.getInt("playerData_activityPoints"),
                data.getString("playerData_regDate"),
                data.getInt("playerData_lastOnline"),
                data.getString("playerData_vip").equals("1"),
                data.getInt("playerData_achievementPoints"),
                data.getInt("playerData_regTimestamp"),
                data.getInt("playerData_favouriteGroup"),
                data.getString("playerData_lastIp"),
                data.getInt("playerData_questId"),
                data.getInt("playerData_forumPosts"),
                data.getString("playerData_newbieStep"));
    }

    @Override
    public void save() {
        ThreadManager.getInstance().execute(this);
    }

    @Override
    public void run() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE players SET " +
                    "username = ?, motto = ?, figure = ?, credits = ?, vip_points = ?, " +
                    "gender = ?, favourite_group = ?, activity_points = ?, quest_id = ?, " +
                    "achievement_points = ?, forum_posts = ?, newbie_step = ? " +
                    "WHERE id = ? " +
                    "LIMIT 1",
                    sqlConnection);

            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, this.motto);
            preparedStatement.setString(3, this.figure);
            preparedStatement.setInt(4, this.credits);
            preparedStatement.setInt(5, this.vipPoints);
            preparedStatement.setString(6, this.gender);
            preparedStatement.setInt(7, this.favouriteGroup);
            preparedStatement.setInt(8, this.activityPoints);
            preparedStatement.setInt(9, this.questId);
            preparedStatement.setInt(10, this.achievementPoints);
            preparedStatement.setInt(11, this.forumPosts);
            preparedStatement.setString(12, this.newbieStep);
            preparedStatement.setInt(13, this.id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public void decreaseCredits(int amount) {
        this.credits -= amount;
    }

    public void increaseCredits(int amount) {
        this.credits += amount;
    }

    public void decreasePoints(int points) {
        this.vipPoints -= points;
    }

    public void increasePoints(int points) {
        this.vipPoints += points;
    }

    public void increaseActivityPoints(int points) {
        this.activityPoints += points;
    }

    public void decreaseActivityPoints(int points) {
        this.activityPoints -= points;
    }

    public void increaseAchievementPoints(int points) {
        this.achievementPoints += points;
    }

    public void setPoints(int points) {
        this.vipPoints = points;
    }

    public int getId() {
        return this.id;
    }

    public int getRank() {
        return this.rank;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAchievementPoints() {
        return this.achievementPoints;
    }

    public String getMotto() {
        return this.motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getFigure() {
        return this.figure;
    }

    public String getGender() {
        return this.gender;
    }

    public int getCredits() {
        return this.credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getVipPoints() {
        return this.vipPoints;
    }

    public int getLastVisit() {
        return this.lastVisit;
    }

    public String getRegDate() {
        return this.regDate;
    }

    public boolean isVip() {
        return this.vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public void setLastVisit(long time) {
        this.lastVisit = (int) time;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getRegTimestamp() {
        return regTimestamp;
    }

    public void setRegTimestamp(int regTimestamp) {
        this.regTimestamp = regTimestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFavouriteGroup() {
        return favouriteGroup;
    }

    public void setFavouriteGroup(int favouriteGroup) {
        this.favouriteGroup = favouriteGroup;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getActivityPoints() {
        return activityPoints;
    }

    public void setActivityPoints(int activityPoints) {
        this.activityPoints = activityPoints;
    }

    public void setVipPoints(int vipPoints) {
        this.vipPoints = vipPoints;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTemporaryFigure() {
        return temporaryFigure;
    }

    public void setTemporaryFigure(String temporaryFigure) {
        this.temporaryFigure = temporaryFigure;
    }

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

    public void setAchievementPoints(int achievementPoints) {
        this.achievementPoints = achievementPoints;
    }

    @Override
    public int getForumPosts() {
        return forumPosts;
    }

    public void increaseForumPosts() {
        ++this.forumPosts;
    }

    public String getNewbieStep() {
        return newbieStep;
    }

    public void setNewbieStep(String newbieStep) {
        this.newbieStep = newbieStep;
    }
}
