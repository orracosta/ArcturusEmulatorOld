package com.habboproject.server.api.game.players.data;

import com.habboproject.server.api.game.players.data.types.IPlaylistItem;
import com.habboproject.server.api.game.players.data.types.IVolumeData;
import com.habboproject.server.api.game.players.data.types.IWardrobeItem;

import java.util.List;

public interface IPlayerSettings {

    IVolumeData getVolumes();

    boolean getHideOnline();

    boolean getHideInRoom();

    boolean getAllowFriendRequests();

    void setAllowFriendRequests(boolean allowFriendRequests);

    boolean getAllowTrade();

    int getHomeRoom();

    void setHomeRoom(int homeRoom);

    List<IWardrobeItem> getWardrobe();

    void setWardrobe(List<IWardrobeItem> wardrobe);

    List<IPlaylistItem> getPlaylist();

    boolean isUseOldChat();

    void setUseOldChat(boolean useOldChat);

    boolean isIgnoreInvites();

    boolean enableEventNotif();

    void setIgnoreInvites(boolean ignoreInvites);
}
