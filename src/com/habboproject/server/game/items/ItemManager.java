package com.habboproject.server.game.items;

import com.habboproject.server.game.items.music.MusicData;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.storage.queries.items.ItemDao;
import com.habboproject.server.storage.queries.items.MusicDao;
import com.habboproject.server.storage.queries.items.TeleporterDao;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.habboproject.server.utilities.Initializable;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class ItemManager implements Initializable {
    private static ItemManager itemManagerInstance;

    private Logger log = Logger.getLogger(ItemManager.class.getName());

    private Map<Integer, ItemDefinition> itemDefinitions;

    private Map<Integer, Integer> itemSpriteIdToDefinitionId;
    private Map<Integer, MusicData> musicData;

    private Map<Long, Integer> itemIdToVirtualId;
    private Map<Integer, Long> virtualIdToItemId;

    private AtomicInteger itemIdCounter;
    private Integer saddleId;

    public ItemManager() {

    }

    @Override
    public void initialize() {
        this.itemDefinitions = new HashMap<>();
        this.musicData = new HashMap<>();

        this.itemIdToVirtualId = new ConcurrentHashMap<>();
        this.virtualIdToItemId = new ConcurrentHashMap<>();

        this.itemIdCounter = new AtomicInteger(1);

        this.loadItemDefinitions();
        this.loadMusicData();

        log.info("ItemManager initialized");
    }

    public static ItemManager getInstance() {
        if (itemManagerInstance == null) {
            itemManagerInstance = new ItemManager();
        }

        return itemManagerInstance;
    }

    public void loadItemDefinitions() {
        Map<Integer, ItemDefinition> tempMap = this.itemDefinitions;
        Map<Integer, Integer> tempSpriteIdItemMap = this.itemSpriteIdToDefinitionId;

        try {
            this.itemDefinitions = ItemDao.getDefinitions();
            this.itemSpriteIdToDefinitionId = new HashMap<>();
        } catch (Exception e) {
            log.error("Error while loading item definitions", e);
        }

        if (tempMap.size() >= 1) {
            tempMap.clear();
            tempSpriteIdItemMap.clear();
        }

        if (this.itemDefinitions != null) {
            for (ItemDefinition itemDefinition : this.itemDefinitions.values()) {
                if(itemDefinition.getItemName().equals("horse_saddle1")) {
                    this.saddleId = itemDefinition.getId();
                }

                this.itemSpriteIdToDefinitionId.put(itemDefinition.getSpriteId(), itemDefinition.getId());
            }
        }

        log.info("Loaded " + this.getItemDefinitions().size() + " item definitions");
    }

    public void loadMusicData() {
        if (!this.musicData.isEmpty()) {
            this.musicData.clear();
        }

        MusicDao.getMusicData(this.musicData);
        log.info("Loaded " + this.musicData.size() + " songs");
    }

    public int getItemVirtualId(long itemId) {
        if(this.itemIdToVirtualId.containsKey(itemId)) {
            return this.itemIdToVirtualId.get(itemId);
        }

        int virtualId = this.itemIdCounter.getAndIncrement();

        this.itemIdToVirtualId.put(itemId, virtualId);
        this.virtualIdToItemId.put(virtualId, itemId);

        return virtualId;
    }

    public void disposeItemVirtualId(long itemId) {
        int virtualId = this.getItemVirtualId(itemId);

        this.itemIdToVirtualId.remove(itemId);
        this.virtualIdToItemId.remove(virtualId);
    }

    public Long getItemIdByVirtualId(int virtualId) {
        return this.virtualIdToItemId.get(virtualId);
    }

    public long getTeleportPartner(long itemId) {
        return TeleporterDao.getPairId(itemId);
    }

    public int roomIdByItemId(long itemId) {
        return RoomItemDao.getRoomIdById(itemId);
    }

    public ItemDefinition getDefinition(int itemId) {
        if (this.getItemDefinitions().containsKey(itemId)) {
            return this.getItemDefinitions().get(itemId);
        }

        return null;
    }

    public MusicData getMusicData(int songId) {
        if (this.musicData.containsKey(songId)) {
            return this.musicData.get(songId);
        }

        return null;
    }

    public MusicData getMusicDataByName(String name) {
        for (MusicData musicData : this.musicData.values()) {
            if (musicData.getName().equals(name)) {
                return musicData;
            }
        }

        return null;
    }

    public Map<Long, Integer> getItemIdToVirtualIds() {
        return itemIdToVirtualId;
    }

    public ItemDefinition getBySpriteId(int spriteId) {
        return this.itemDefinitions.get(this.itemSpriteIdToDefinitionId.get(spriteId));
    }

    public Logger getLogger() {
        return log;
    }

    public Map<Integer, ItemDefinition> getItemDefinitions() {
        return this.itemDefinitions;
    }

    public Integer getSaddleId() {
        return saddleId;
    }
}
