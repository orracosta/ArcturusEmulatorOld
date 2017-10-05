package com.habboproject.server.api.game.players;

import com.habboproject.server.api.game.players.data.IPlayerData;
import com.habboproject.server.api.game.players.data.IPlayerSettings;
import com.habboproject.server.api.game.players.data.IPlayerStatistics;
import com.habboproject.server.api.game.players.data.components.PlayerBots;
import com.habboproject.server.api.game.players.data.components.PlayerInventory;
import com.habboproject.server.api.game.players.data.components.PlayerPermissions;
import com.habboproject.server.api.game.rooms.entities.PlayerRoomEntity;
import com.habboproject.server.api.networking.messages.IMessageComposer;
import com.habboproject.server.api.networking.sessions.BaseSession;

import java.util.List;

public interface BasePlayer {
    int INFINITE_BALANCE = 999999;

    void dispose();

    void sendBalance();

    IMessageComposer composeCreditBalance();

    IMessageComposer composeCurrenciesBalance();

    void loadRoom(int id, String password);

    void poof();

    void ignorePlayer(int playerId);

    void unignorePlayer(int playerId);

    boolean ignores(int playerId);

    List<Integer> getRooms();

    void setRooms(List<Integer> rooms);

    void setSession(BaseSession client);

    PlayerRoomEntity getEntity();

    BaseSession getSession();

    IPlayerData getData();

    IPlayerSettings getSettings();

    IPlayerStatistics getStats();

    PlayerPermissions getPermissions();

//    MessengerComponent getMessenger();
//
    PlayerInventory getInventory();
//
//    SubscriptionComponent getSubscription();
//
//    RelationshipComponent getRelationships();
//
    PlayerBots getBots();
//
//    PetComponent getPets();
//
//    QuestComponent getQuests();

    int getId();

    void sendNotif(String title, String message);

    void sendMotd(String message);

    boolean isTeleporting();

    long getTeleportId();

    void setTeleportId(long teleportId);

    long getRoomLastMessageTime();

    void setRoomLastMessageTime(long roomLastMessageTime);

    double getRoomFloodTime();

    void setRoomFloodTime(double roomFloodTime);

    int getRoomFloodFlag();

    void setRoomFloodFlag(int roomFloodFlag);

    String getLastMessage();

    void setLastMessage(String lastMessage);

    List<Integer> getGroups();

    int getNotifCooldown();

    void setNotifCooldown(int notifCooldown);

    int getLastRoomId();

    void setLastRoomId(int lastRoomId);

    int getLastGift();

    void setLastGift(int lastGift);

    long getMessengerLastMessageTime();

    void setMessengerLastMessageTime(long messengerLastMessageTime);

    double getMessengerFloodTime();

    void setMessengerFloodTime(double messengerFloodTime);

    int getMessengerFloodFlag();

    void setMessengerFloodFlag(int messengerFloodFlag);

    boolean isDeletingGroup();

    void setDeletingGroup(boolean isDeletingGroup);

    long getDeletingGroupAttempt();

    void setDeletingGroupAttempt(long deletingGroupAttempt);

    void bypassRoomAuth(boolean bypassRoomAuth);

    boolean isBypassingRoomAuth();

    int getLastFigureUpdate();

    void setLastFigureUpdate(int lastFigureUpdate);

    long getLastReward();

    void setLastReward(long lastReward);
}
