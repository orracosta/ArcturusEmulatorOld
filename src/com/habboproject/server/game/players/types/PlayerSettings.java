package com.habboproject.server.game.players.types;

import com.habboproject.server.api.game.players.data.IPlayerSettings;
import com.habboproject.server.api.game.players.data.types.IPlaylistItem;
import com.habboproject.server.api.game.players.data.types.IVolumeData;
import com.habboproject.server.api.game.players.data.types.IWardrobeItem;
import com.habboproject.server.game.players.components.types.settings.NavigatorData;
import com.habboproject.server.game.players.components.types.settings.PlaylistItem;
import com.habboproject.server.game.players.components.types.settings.VolumeData;
import com.habboproject.server.game.players.components.types.settings.WardrobeItem;
import com.habboproject.server.utilities.JsonFactory;
import com.google.gson.reflect.TypeToken;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PlayerSettings implements IPlayerSettings {
    private VolumeData volumes;
    private NavigatorData navigator;

    private List<IWardrobeItem> wardrobe;
    private List<IPlaylistItem> playlist;

    private boolean hideOnline;
    private boolean hideInRoom;
    private boolean allowFriendRequests;
    private boolean allowTrade;

    private int homeRoom;
    private boolean useOldChat;
    private boolean ignoreInvites;
    private boolean cameraFollow;

    private boolean enableEventNotif = true;

    public PlayerSettings(ResultSet data, boolean isLogin) throws SQLException {
        if (isLogin) {
            String volumeData = data.getString("playerSettings_volume");
            this.volumes = volumeData != null && volumeData.startsWith("{") ? JsonFactory.getInstance().fromJson(volumeData, VolumeData.class) : new VolumeData(100, 100, 100);

            String navigatorData = data.getString("playerSettings_navigator");
            this.navigator = navigatorData != null && navigatorData.startsWith("{") ? JsonFactory.getInstance().fromJson(navigatorData, NavigatorData.class) : new NavigatorData(68, 42, 425, 592, false);

            this.hideOnline = data.getString("playerSettings_hideOnline").equals("1");
            this.hideInRoom = data.getString("playerSettings_hideInRoom").equals("1");
            this.allowFriendRequests = data.getString("playerSettings_allowFriendRequests").equals("1");
            this.allowTrade = data.getString("playerSettings_allowTrade").equals("1");

            this.homeRoom = data.getInt("playerSettings_homeRoom");

            String wardrobeText = data.getString("playerSettings_wardrobe");

            if (wardrobeText == null || wardrobeText.isEmpty()) {
                wardrobe = new ArrayList<>();
            } else {
                wardrobe = JsonFactory.getInstance().fromJson(wardrobeText, new TypeToken<ArrayList<WardrobeItem>>() {
                }.getType());
            }

            String playlistText = data.getString("playerSettings_playlist");

            if (playlistText == null || playlistText.isEmpty()) {
                playlist = new ArrayList<>();
            } else {
                playlist = JsonFactory.getInstance().fromJson(playlistText, new TypeToken<ArrayList<PlaylistItem>>() {
                }.getType());
            }

            this.useOldChat = data.getString("playerSettings_useOldChat").equals("1");
            this.ignoreInvites = data.getString("playerSettings_ignoreInvites").equals("1");
            this.cameraFollow = data.getString("playerSettings_cameraFollow").equals("1");
        } else {
            String volumeData = data.getString("volume");
            this.volumes = volumeData != null && volumeData.startsWith("{") ? JsonFactory.getInstance().fromJson(volumeData, VolumeData.class) : new VolumeData(100, 100, 100);

            String navigatorData = data.getString("navigator");
            this.navigator = navigatorData != null && navigatorData.startsWith("{") ? JsonFactory.getInstance().fromJson(navigatorData, NavigatorData.class) : new NavigatorData(68, 42, 425, 592, false);

            this.hideOnline = data.getString("hide_online").equals("1");
            this.hideInRoom = data.getString("hide_inroom").equals("1");
            this.allowFriendRequests = data.getString("allow_friend_requests").equals("1");
            this.allowTrade = data.getString("allow_trade").equals("1");

            this.homeRoom = data.getInt("home_room");

            String wardrobeText = data.getString("wardrobe");

            if (wardrobeText == null || wardrobeText.isEmpty()) {
                wardrobe = new ArrayList<>();
            } else {
                wardrobe = JsonFactory.getInstance().fromJson(wardrobeText, new TypeToken<ArrayList<WardrobeItem>>() {
                }.getType());
            }

            String playlistText = data.getString("playlist");

            if (playlistText == null || playlistText.isEmpty()) {
                playlist = new ArrayList<>();
            } else {
                playlist = JsonFactory.getInstance().fromJson(playlistText, new TypeToken<ArrayList<PlaylistItem>>() {
                }.getType());
            }

            this.useOldChat = data.getString("chat_oldstyle").equals("1");
            this.ignoreInvites = data.getString("ignore_invites").equals("1");
            this.cameraFollow = data.getString("camera_follow").equals("1");
        }
    }

    public PlayerSettings() {
        this.volumes = new VolumeData(100, 100, 100);
        this.navigator = new NavigatorData(68, 42, 425, 592, false);
        this.hideInRoom = false;
        this.homeRoom = 0;
        this.hideOnline = false;
        this.allowFriendRequests = true;
        this.allowTrade = true;
        this.wardrobe = new ArrayList<>();
        this.playlist = new ArrayList<>();
        this.useOldChat = false;
        this.cameraFollow = false;
    }

    public IVolumeData getVolumes() {
        return this.volumes;
    }

    public NavigatorData getNavigator() {
        return navigator;
    }

    public boolean getHideOnline() {
        return this.hideOnline;
    }

    public boolean getHideInRoom() {
        return this.hideInRoom;
    }

    public boolean getAllowFriendRequests() {
        return this.allowFriendRequests;
    }

    public void setAllowFriendRequests(boolean allowFriendRequests) {
        this.allowFriendRequests = allowFriendRequests;
    }

    public boolean getAllowTrade() {
        return this.allowTrade;
    }

    public int getHomeRoom() {
        return this.homeRoom;
    }

    public void setHomeRoom(int homeRoom) {
        this.homeRoom = homeRoom;
    }

    public List<IWardrobeItem> getWardrobe() {
        return wardrobe;
    }

    public void setWardrobe(List<IWardrobeItem> wardrobe) {
        this.wardrobe = wardrobe;
    }

    public List<IPlaylistItem> getPlaylist() {
        return playlist;
    }

    public boolean isUseOldChat() {
        return this.useOldChat;
    }

    public void setUseOldChat(boolean useOldChat) {
        this.useOldChat = useOldChat;
    }

    public boolean isIgnoreInvites() {
        return ignoreInvites;
    }

    public void setIgnoreInvites(boolean ignoreInvites) {
        this.ignoreInvites = ignoreInvites;
    }

    public boolean enableEventNotif() {
        return this.enableEventNotif;
    }

    public void setEnableEventNotif(boolean enableEventNotif) {
        this.enableEventNotif = enableEventNotif;
    }

    public boolean cameraFollow() {
        return this.cameraFollow;
    }

    public void setCameraFollow(boolean cameraFollow) {
        this.cameraFollow = cameraFollow;
    }
}
