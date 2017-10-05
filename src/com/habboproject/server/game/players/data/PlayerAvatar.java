package com.habboproject.server.game.players.data;

public interface PlayerAvatar {
    public static final byte USERNAME_FIGURE = 0;
    public static final byte USERNAME_FIGURE_MOTTO = 1;

    public int getId();

    public String getUsername();

    public void setUsername(String username);

    public String getFigure();

    public void setFigure(String figure);

    public String getMotto();

    public void setMotto(String motto);

    public int getForumPosts();
}