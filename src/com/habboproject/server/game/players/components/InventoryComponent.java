package com.habboproject.server.game.players.components;

import com.habboproject.server.api.game.furniture.types.GiftItemData;
import com.habboproject.server.api.game.furniture.types.LimitedEditionItem;
import com.habboproject.server.api.game.furniture.types.SongItem;
import com.habboproject.server.api.game.players.data.components.PlayerInventory;
import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.music.SongItemData;
import com.habboproject.server.game.players.components.types.inventory.InventoryItem;
import com.habboproject.server.game.players.components.types.inventory.InventoryItemSnapshot;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.network.messages.outgoing.catalog.UnseenItemsMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.wired.WiredRewardMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.BadgeInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.RemoveObjectFromInventoryMessageComposer;
import com.habboproject.server.storage.queries.achievements.PlayerAchievementDao;
import com.habboproject.server.storage.queries.player.inventory.InventoryDao;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryComponent implements PlayerInventory {
    private Player player;

    private Map<Long, PlayerItem> floorItems;
    private Map<Long, PlayerItem> wallItems;
    private Map<String, Integer> badges;

    private boolean itemsLoaded = false;

    private Logger log = Logger.getLogger(InventoryComponent.class.getName());

    public InventoryComponent(Player player) {
        this.player = player;

        this.floorItems = new ConcurrentHashMap<>();
        this.wallItems = new ConcurrentHashMap<>();
        this.badges = new ConcurrentHashMap<>();

        this.loadBadges();
    }

    @Override
    public void loadItems() {
        this.itemsLoaded = true;

        if (this.getWallItems().size() >= 1) {
            this.getWallItems().clear();
        }
        if (this.getFloorItems().size() >= 1) {
            this.getFloorItems().clear();
        }

        try {
            Map<Long, PlayerItem> inventoryItems = InventoryDao.getInventoryByPlayerId(this.player.getId());

            for (Map.Entry<Long, PlayerItem> item : inventoryItems.entrySet()) {
                if (item.getValue().getDefinition().getType().equals("s")) {
                    this.getFloorItems().put(item.getKey(), item.getValue());
                }

                if (item.getValue().getDefinition().getType().equals("i")) {
                    this.getWallItems().put(item.getKey(), item.getValue());
                }
            }
        } catch (Exception e) {
            log.error("Error while loading user inventory", e);
        }
    }

    @Override
    public void loadBadges() {
        try {
            // TODO: redo this so we can seperate achievement badges to other badges. Maybe a "badge type" or something.
            this.badges = InventoryDao.getBadgesByPlayerId(player.getId());
        } catch (Exception e) {
            log.error("Error while loading user badges");
        }
    }

    @Override
    public void addBadge(String code, boolean insert) {
        this.addBadge(code, insert, true);
    }

    @Override
    public void addBadge(String code, boolean insert, boolean sendAlert) {
        if (!badges.containsKey(code)) {
            if (insert) {
                InventoryDao.addBadge(code, this.player.getId());
            }

            this.badges.put(code, 0);

            this.player.getSession().
                    send(new BadgeInventoryMessageComposer(this.getBadges())).
                    send(new UnseenItemsMessageComposer(new HashMap<Integer, List<Integer>>() {{
                        put(4, Lists.newArrayList(0));
                    }}));

            if (sendAlert) {
                this.player.getSession().send(new WiredRewardMessageComposer(7));
            }
        }
    }

    @Override
    public boolean hasBadge(String code) {
        return this.badges.containsKey(code);
    }

    @Override
    public void removeBadge(String code, boolean delete) {
        this.removeBadge(code, delete, true, true);
    }

    @Override
    public void removebadge(String code, boolean delete, boolean sendAlert) {
        this.removeBadge(code, delete, sendAlert, true);
    }

    @Override
    public void removeBadge(String code, boolean delete, boolean sendAlert, boolean sendUpdate) {
        if (badges.containsKey(code)) {
            if (delete) {
                InventoryDao.removeBadge(code, player.getId());
            }

            this.badges.remove(code);

            if (sendAlert) {
                this.player.getSession().send(new AlertMessageComposer(Locale.get("badge.deleted")));
            }

            this.player.getSession().send(new BadgeInventoryMessageComposer(this.badges));
        }
    }

    @Override
    public void achievementBadge(String achievement, int level) {
        final String oldBadge = achievement + (level - 1);
        final String newBadge = achievement + level;

        boolean isUpdated = false;

        if (this.badges.containsKey(oldBadge)) {
            this.removeBadge(oldBadge, false, false, false);

            PlayerAchievementDao.updateBadge(oldBadge, newBadge, this.player.getId());
            isUpdated = true;
        }

        this.addBadge(newBadge, !isUpdated, false);
    }

    @Override
    public void resetBadgeSlots() {
        for (Map.Entry<String, Integer> badge : this.badges.entrySet()) {
            if (badge.getValue() != 0) {
                this.badges.replace(badge.getKey(), 0);
            }
        }
    }

    @Override
    public Map<String, Integer> equippedBadges() {
        Map<String, Integer> badges = new ConcurrentHashMap<>();

        for (Map.Entry<String, Integer> badge : this.getBadges().entrySet()) {
            if (badge.getValue() > 0)
                badges.put(badge.getKey(), badge.getValue());
        }

        return badges;
    }

    @Override
    public PlayerItem add(long id, int itemId, int groupId, String extraData, GiftItemData giftData, LimitedEditionItem limitedEditionItem) {
        PlayerItem item = new InventoryItem(id, itemId, groupId, extraData, giftData, limitedEditionItem);

        if (item.getDefinition().getType().equals("s")) {
            this.getFloorItems().put(id, (InventoryItem) item);
        }

        if (item.getDefinition().getType().equals("i")) {
            this.getWallItems().put(id, (InventoryItem) item);
        }

        return item;
    }

    @Override
    public List<SongItem> getSongs() {
        List<SongItem> songItems = Lists.newArrayList();

        for (PlayerItem inventoryItem : this.floorItems.values()) {
            if (inventoryItem.getDefinition().isSong()) {
                songItems.add(new SongItemData((InventoryItemSnapshot) inventoryItem.createSnapshot(), inventoryItem.getDefinition().getSongId()));
            }
        }

        return songItems;
    }

    @Override
    public void add(long id, int itemId, int groupId, String extraData, LimitedEditionItem limitedEditionItem) {
        add(id, itemId, groupId, extraData, null, limitedEditionItem);
    }

    @Override
    public void addItem(PlayerItem item) {
        if ((this.floorItems.size() + this.wallItems.size()) >= 5000) {
            this.getPlayer().sendNotif("Notice", Locale.getOrDefault("game.inventory.limitExceeded", "You have over 5,000 items in your inventory. The next time you login, you will only see the first 5000 items."));
        }

        if (item.getDefinition().getType().equals("s"))
            floorItems.put(item.getId(), (InventoryItem) item);
        else if (item.getDefinition().getType().equals("i"))
            wallItems.put(item.getId(), (InventoryItem) item);
    }

    @Override
    public void removeItem(PlayerItem item) {
        if (item.getDefinition().getType().equals("s"))
            floorItems.remove(item.getId());
        else if (item.getDefinition().getType().equals("i"))
            wallItems.remove(item.getId());
    }

    @Override
    public void removeFloorItem(long itemId) {
        if (this.getFloorItems() == null) {
            return;
        }

        this.getFloorItems().remove(itemId);
        this.getPlayer().getSession().send(new RemoveObjectFromInventoryMessageComposer(ItemManager.getInstance().getItemVirtualId(itemId)));
    }

    @Override
    public void removeWallItem(long itemId) {
        this.getWallItems().remove(itemId);
        this.getPlayer().getSession().send(new RemoveObjectFromInventoryMessageComposer(ItemManager.getInstance().getItemVirtualId(itemId)));
    }

    @Override
    public boolean hasFloorItem(long id) {
        return this.getFloorItems().containsKey(id);
    }

    @Override
    public PlayerItem getFloorItem(long id) {
        if (!this.hasFloorItem(id)) {
            return null;
        }
        return this.getFloorItems().get(id);
    }

    @Override
    public boolean hasWallItem(long id) {
        return this.getWallItems().containsKey(id);
    }

    @Override
    @Deprecated
    public PlayerItem getWallItem(int id) {
        return getWallItem(ItemManager.getInstance().getItemIdByVirtualId(id));
    }

    @Override
    @Deprecated
    public PlayerItem getFloorItem(int id) {
        return getFloorItem(ItemManager.getInstance().getItemIdByVirtualId(id));
    }

    @Override
    public PlayerItem getWallItem(long id) {
        if (!this.hasWallItem(id)) {
            return null;
        }
        return this.getWallItems().get(id);
    }

    @Override
    public PlayerItem getItem(long id) {
        PlayerItem item = getFloorItem(id);

        if (item != null) {
            return item;
        }

        return getWallItem(id);
    }

    @Override
    public void dispose() {
        for(PlayerItem floorItem : this.floorItems.values()) {
            ItemManager.getInstance().disposeItemVirtualId(floorItem.getId());
        }

        for(PlayerItem wallItem : this.wallItems.values()) {
            ItemManager.getInstance().disposeItemVirtualId(wallItem.getId());
        }

        this.floorItems.clear();
        this.floorItems = null;

        this.wallItems.clear();
        this.wallItems = null;

        this.badges.clear();
        this.badges = null;
    }

    @Override
    public int getTotalSize() {
        return this.getWallItems().size() + this.getFloorItems().size();
    }

    @Override
    public Map<Long, PlayerItem> getWallItems() {
        return this.wallItems;
    }

    @Override
    public Map<Long, PlayerItem> getFloorItems() {
        return this.floorItems;
    }

    @Override
    public Map<String, Integer> getBadges() {
        return this.badges;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean itemsLoaded() {
        return itemsLoaded;
    }
}
