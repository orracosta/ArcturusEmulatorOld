package com.habboproject.server.game.players.data;

import com.habboproject.server.game.utilities.validator.PlayerFigureValidator;

public class PlayerAvatarData implements PlayerAvatar {
    private int id;
    private String username;
    private String figure;
    private String motto;
    private int forumPosts;

    public PlayerAvatarData(int id, String username, String figure, String motto, int forumPosts) {
        this.id = id;
        this.username = username;
        this.figure = figure;
        this.motto = motto;
        this.forumPosts = forumPosts;

        if(figure == null) { return; }

        if(!PlayerFigureValidator.isValidFigureCode(this.figure, "m")) {
            this.figure = PlayerData.DEFAULT_FIGURE;
        }
    }

    public PlayerAvatarData(int id, String username, String figure, int forumPosts) {
        this(id, username, figure, null, forumPosts);
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getFigure() {
        return figure;
    }

    @Override
    public void setFigure(String figure) {
        this.figure = figure;
    }

    @Override
    public String getMotto() {
        return motto;
    }

    @Override
    public void setMotto(String motto) {
        this.motto = motto;
    }

    @Override
    public int getForumPosts() {
        return forumPosts;
    }
}
