package com.habboproject.server.api.game.players.data.components;

import com.habboproject.server.api.game.furniture.types.GiftItemData;
import com.habboproject.server.api.game.furniture.types.LimitedEditionItem;
import com.habboproject.server.api.game.furniture.types.SongItem;
import com.habboproject.server.api.game.players.data.PlayerComponent;
import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;

import java.util.List;
import java.util.Map;

public interface PlayerInventory extends PlayerComponent {
    void loadItems();

    void loadBadges();

    void addBadge(String code, boolean insert);

    void addBadge(String code, boolean insert, boolean sendAlert);

    boolean hasBadge(String code);

    void removeBadge(String code, boolean delete);

    void removebadge(String code, boolean delete, boolean sendAlert);

    void removeBadge(String code, boolean delete, boolean sendAlert, boolean sendUpdate);

    void achievementBadge(String achievement, int level);

    void resetBadgeSlots();

    Map<String, Integer> equippedBadges();

    PlayerItem add(long id, int itemId, int groupId, String extraData, GiftItemData giftData, LimitedEditionItem limitedEditionItem);

    List<SongItem> getSongs();

    void add(long id, int itemId, int groupId, String extraData, LimitedEditionItem limitedEditionItem);

    void addItem(PlayerItem item);

    void removeItem(PlayerItem item);

    void removeFloorItem(long itemId);

    void removeWallItem(long itemId);

    boolean hasFloorItem(long id);

    PlayerItem getFloorItem(long id);

    boolean hasWallItem(long id);

    @Deprecated
    PlayerItem getWallItem(int id);

    @Deprecated
    PlayerItem getFloorItem(int id);

    PlayerItem getWallItem(long id);

    PlayerItem getItem(long id);

    int getTotalSize();

    Map<Long, PlayerItem> getWallItems();

    Map<Long, PlayerItem> getFloorItems();

    Map<String, Integer> getBadges();

    boolean itemsLoaded();
}
