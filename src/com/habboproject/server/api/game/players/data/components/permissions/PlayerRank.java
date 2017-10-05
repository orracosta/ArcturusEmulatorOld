package com.habboproject.server.api.game.players.data.components.permissions;

public interface PlayerRank {
    public int getId();

    public String getName();

    public boolean floodBypass();

    public int floodTime();

    public boolean disconnectable();

    public boolean bannable();

    public boolean modTool();

    public boolean roomKickable();

    public boolean roomFullControl();

    public boolean roomMuteBypass();

    public boolean roomFilterBypass();

    public boolean roomIgnorable();

    public boolean roomEnterFull();

    public boolean roomEnterLocked();

    public boolean roomStaffPick();

    public boolean messengerStaffChat();

    public int messengerMaxFriends();

    public boolean aboutDetailed();

    public boolean aboutStats();

    public boolean isAmbassador();

    public boolean isHelper();
}
